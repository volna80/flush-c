package com.volna80.betfair.api.impl;

import com.volna80.betfair.api.APINGException;
import com.volna80.betfair.api.BetfairException;
import com.volna80.betfair.api.model.IMsgBuilder;
import com.volna80.betfair.api.model.Ssoid;
import com.volna80.betfair.api.model.errors.ErrorCode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.OK;

/**
 * @author nikolay.volnov@gmail.com
 */
public abstract class AbstractAPI {

    public static final String EMPTY_REQUEST = "{}";
    private static final Logger log = LoggerFactory.getLogger(AbstractAPI.class);
    protected final String uri;
    protected final String appId;
    protected final Ssoid ssoid;
    protected final Client client;

    public AbstractAPI(String appId, Ssoid ssoid, Client client, String uri) {
        this.uri = uri;
        this.client = client;
        this.appId = appId;
        this.ssoid = ssoid;
    }

    protected <T> T post(final Invocation.Builder builder, final IMsgBuilder<T> converter, final String request) throws BetfairException {
        log.debug("[{}] req : {}", ssoid, request);
        Response response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON));
        try {
            if (OK.getStatusCode() == response.getStatus()) {
                final String str = response.readEntity(String.class);

                if (str == null || str.isEmpty()) {
                    log.error("empty response {} for request {} ", response, request);
                }

                log.debug("[{}] rsp : {}", ssoid, str);
                return converter.build(str);
            } else {
                //ScopedJaxrsResponse{ClientResponse{method=POST, uri=https://api.betfair.com/exchange/betting/rest/v1.0/listCurrentOrders/, status=400, reason=Bad Request}}:
                // {"detail":{"exceptionname":"APINGException","APINGException":{"errorDetails":"","errorCode":"INVALID_SESSION_INFORMATION","requestUUID":"prdang-28828c-08111300-00038b2337"}},"faultcode":"Client","faultstring":"ANGX-0003"}

                log.error("[{}] " + response.getStatus() + ". request: {}, response: {}", ssoid, request, response);

                if (Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {

                    try {
                        final String str = response.readEntity(String.class);
                        JSONObject json = new JSONObject(str);
                        JSONObject detail = json.getJSONObject("detail");
                        if (detail.getJSONObject("APINGException") != null) {
                            JSONObject apiNgException = detail.getJSONObject("APINGException");
                            ErrorCode code = ErrorCode.valueOf(apiNgException.getString("errorCode"));

                            throw new APINGException("" + str, code);
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        log.info("couldn't parse an error response. {}. Error msg: ", response, e.getMessage());
                        throw new BetfairException("couldn't request  : " + response + ":" + response.readEntity(String.class));
                    }
                }

                throw new BetfairException("couldn't request  : " + response + ":" + response.readEntity(String.class));


            }
        } finally {
            response.close();
        }
    }

    protected Invocation.Builder makeRequest(String path) {
        WebTarget target = client.target(uri).path(path);
        Invocation.Builder request = target.request(MediaType.APPLICATION_JSON_TYPE);
        request.header("X-Application", appId);
        request.header("X-Authentication", ssoid.getId());
        return request;
    }
}
