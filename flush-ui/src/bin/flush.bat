java --module-path ./lib --add-modules javafx.base,javafx.graphics --add-reads javafx.base=ALL-UNNAMED --add-reads javafx.graphics=ALL-UNNAMED --add-opens java.base/java.lang=com.google.guice -Dbinary.css=false -Dlogback.configurationFile=./conf/logback.xml -m flush.ui/com.volna80.flush.ui.Flush