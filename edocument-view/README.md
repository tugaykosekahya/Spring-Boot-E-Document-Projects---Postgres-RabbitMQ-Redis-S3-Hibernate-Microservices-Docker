Gradle -> 7.3.3
Java -> 17.0.2

--------------------------------------------------------------------------------------------
config -->
ApiSecurityConfig --> uygulama guvenlik ayarlari
EmptyStringToNullConverter --> veri tabanına boş string i null olarak atar. 
ExceptionHandlerConfig --> global exception handler icin
LivenessEventListener --> liveness probe icin (pod ayaga kalkarken kontrol edilir)
MessagesConfig --> mesajlari properties dosyasindan okumak icin
OpenApiConfig --> swagger ayarlari
ReadinessEventListener --> readiness probe icin (pod ayaga kalkarken kontrol edilir)
StrictIntegerDeserializer --> requstte gelen json icin validation yaparken strict type kontrolu yapar
StrictStringDeserializer --> requstte gelen json icin validation yaparken strict type kontrolu yapar
PostreDBConfig --> db ayarlari

cache -->
local cacheler

external -->
dis servisler icin request response objeleri ve servis bulunur.

object -->
request response objeleri bulunur.

service -->
servis bulunur.

dao -->
jpa repolar ve jpa queryler bulunur.

mapper -->
entityleri (persistence object leri) dto ya cevirmek icin veya tam tersi.
	
--------------------------------------------------------------------------------------------

http://git.gelbim.gov.tr/Deploy/evdbelge-deploy
Deploylar buradan yapılır.

http://git.gelbim.gov.tr/EVDB/evdbelge/evdbelge-goruntuleme

Ana proje versiyonu ile deploy projesi versiyonu aynı olmalıdır. Ana projede oluşan image o versiyona göre nexustan çekilir ve kubernetese deploy edilir.


----------------------------------------------------------------------------------------------

10.251.55.131 den evdb-runner kullanıcısıyla config yaml dosyaları deploy edilebilir.
örn: kubectl apply -f hazirlama-application-config-map.yaml --context=evdbelge-test

config dosyaları deploy projesi altında güncel olmalıdır ve sistem ekibine de güncel halleri ayrıca gönderilir.


----------------------------------------------------------------------------------------------

Local repository de jar oluşturmak için gradle tasklarındaki publishing -> publishToMavenLocal

Bootable jar çıkarılmak istenmiyorsa bootJar enabled=false , jar enabled=true yapılmalı

Dependency leri içinde oluşması isteniyorsa:


jar {

    manifest {
        attributes "Main-Class": "tr.gov.gib.interaktiflogwatch.InteraktifLogWatchApplication"
    }
    enabled = true
    archiveClassifier = ''
    into("META-INF/maven/$project.group/$project.name") {
        from { generatePomFileForMavenJavaPublication }
        rename ".*", "pom.xml"
    }
}


----------------------------------------------------------------------------------------------

Swagger:

http://localhost:8074/goruntuleme-server/swagger-ui/index.html#/
veya
http://localhost:8074/goruntuleme-server/api

TEST

PROD

----------------------------------------------------------------------------------------------


```text
GORUNTULEME_SERVER_PORT=8074;

SVR_CONTEXT_PATH=/evdb/evdbelge/goruntuleme-server;
LOG_PATH=evdbelge-goruntuleme;
ENVIRONMENT=TEST;

APP_NAME=goruntuleme;

LOG_HIBERNATE_SQL=trace;

POSTGRES_EVDBELGE_DATABASE_URL=jdbc:postgresql://10.251.59.58:5432/evdbelge?currentSchema=evdbelge;
POSTGRES_EVDBELGE_DATABASE_USERNAME=evdbelge_owner;
POSTGRES_EVDBELGE_DATABASE_PASSWORD=eVdBelge12+3;
POSTGRES_DATABASE_SHOW_SQL=true;

ADMIN_USER_NAME=admin;
ADMIN_PASSWORD=@dmiN123;
ADMIN_ROLE=ADMIN;
EVDBELGE_USER_NAME=evdbelge;
EVDBELGE_PASSWORD=evdbelge123;
EVDBELGE_ROLE=EVDBELGE;

SERVICE_URL=http://localhost:8074/evdb/evdbelge/goruntuleme-server;


ELASTIC_APM_ENABLE=false;
ELASTIC_APM_APP_PACKAGE=;
ELASTIC_APM_SERVICE_URL=;
ELASTIC_APM_SECRET_TOKEN=;
ELASTIC_APM_VERIFY_SERVER_CERT=;

REDIS_SERVICE_HOST=10.251.55.127;
REDIS_SERVICE_PASSWORD=EVdBelge_ReD_123*;
REDIS_SERVICE_PORT=32173;

S3_FILE_SEPERATOR=_;
S3_FILE_EXTENSION=.json;
S3_ENDPOINT=http://ecs2.gelbim.gov.tr;
S3_NAMESPACE=test_gib_evdbbelge;
S3_ACCESS_KEY=evdbbelge_test_user1;
S3_SECRET_KEY=mAfswaArz2Dqqf9GQrHfuG8rm75NhCAAkGjk8yRg;
S3_BUCKET_NAME_PREFIX=evdbbelge-test-;

TRIPLEDES_KEY=nqU6I0CsLpz+olU0m6/6QVPpTdUyeYGQ;
TRIPLEDES_IV=xc5r/e2zrQU=;
```


----------------------------------------------------------------------------------------------


Eğer localde hem gradle 7.3.3 hem de gradle 7.0 dan düşük versiyon kullanılacaksa --> C:\Users\CICEK_BERK\.gradle içindeki init.gradle kullanılmamalı.

Gradle neredeyse onun içine init.gradle dosyası koyulur.

C:\gradle-7.3.3\init.d


7.3.3 için init.gradle

----------------------------------------------------------------------------------------------


allprojects {   
apply plugin: 'java-library'
apply plugin: 'maven-publish' 
    repositories {
		mavenLocal()
		maven {
          name = 'standard-nexus-maven-public'
          url = 'http://nexustest.gelbim.gov.tr:2081/repository/maven-public/'
		  allowInsecureProtocol = true
		  credentials {
			 username "developer"
			 password "developer"
		  }
        }
    }
     
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
        resolutionStrategy.cacheDynamicVersionsFor 0, 'MINUTES'
    }
	
	publishing{
		publications {
			maven(MavenPublication) {
				from components.java
			}
		}
	}

}

settingsEvaluated { settings ->
    settings.pluginManagement {
        repositories {
            maven {
                url 'http://nexustest.gelbim.gov.tr:2081/repository/maven-public/'
				allowInsecureProtocol = true
				credentials {
				     username "developer"
				     password "developer"
			    }
            }
            gradlePluginPortal()
        }
    }
}

