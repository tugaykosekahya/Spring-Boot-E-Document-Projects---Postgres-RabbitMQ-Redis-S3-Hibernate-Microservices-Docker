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

Ana proje versiyonu ile deploy projesi versiyonu aynı olmalıdır. Ana projede oluşan image o versiyona göre nexustan çekilir ve kubernetese deploy edilir.

----------------------------------------------------------------------------------------------

örn: kubectl apply -f application-config-map.yaml --context=test

config dosyaları deploy projesi altında güncel olmalıdır ve sistem ekibine de güncel halleri ayrıca gönderilir.

----------------------------------------------------------------------------------------------

Local repository de jar oluşturmak için gradle tasklarındaki publishing -> publishToMavenLocal

Bootable jar çıkarılmak istenmiyorsa bootJar enabled=false , jar enabled=true yapılmalı


----------------------------------------------------------------------------------------------

Swagger:

http://localhost:8070/swagger-ui/index.html#/
veya
http://localhost:8070/api

TEST

PROD

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
          url = ':2081/repository/maven-public/'
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
                url ':2081/repository/maven-public/'
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

