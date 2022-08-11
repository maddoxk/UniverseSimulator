echo "[RUN] Running App"

mvn clean package

java -cp target/com.maddoxk.locus-0.1-Alpha.jar com.maddoxk.locus.App