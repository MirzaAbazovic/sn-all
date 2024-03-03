rm -rf build
mkdir build
mkdir build/modules
mkdir build/modulesWin32
# mkdir build/images

export JAVA_HOME=/Users/halidzehic/java_jdk_14
export PATH=$JAVA_HOME/bin:$PATH

jmod create --class-path ../funmodel-app/target/funmodel-app-0.0.1-SNAPSHOT.jar --main-class com.bitconex.danalyst.funmodmap.gui.FunModelMain build/modules/funmodel-jmod.jmod

# jlink --output build/images --module-path build/modules:/Users/halidzehic/apps/javafx-sdk-15.0.1/lib:/Users/halidzehic/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.12.0:/Users/halidzehic/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.12.0:/Users/halidzehic/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.12.0 --add-modules funmodel.app --launcher FunModelApp=funmodel.app

# jpackage -n FunModelApp --runtime-image build/images -m funmodel.app/com.bitconex.danalyst.funmodmap.gui.FunModelMain

jmod create --target-platform windows-amd64  --class-path ../funmodel-app/target/funmodel-app-0.0.1-SNAPSHOT.jar --main-class com.bitconex.danalyst.funmodmap.gui.FunModelMain build/modulesWin32/funmodel-jmod.jmod

jlink --output build/funModAppWin32 --module-path build/modulesWin32:/Users/halidzehic/apps/crossJDK/win/jdk-14.0.2/jmods:/Users/halidzehic/apps/crossJDK/win/javafx-jmods-15.0.1:/Users/halidzehic/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.12.0:/Users/halidzehic/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.12.0:/Users/halidzehic/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.12.0 --add-modules funmodel.app --launcher FunModelApp=funmodel.app

cd build

zip -r funModAppWin32.zip funModAppWin32
