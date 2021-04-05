# Spring Boot Minio Example
> Spring Boot File Rest Api Example Using Minio
>
<img src="https://github.com/susimsek/spring-boot-minio-example/blob/main/images/spring-boot-minio-example.png" alt="Spring Boot Minio Example" width="100%" height="100%"/> 

## Prerequisites

* Java 11
* Maven 3.3+
* Docker 19.03+
* Docker Compose 1.25+

## Installation


```sh
./mvnw compile jib:dockerBuild
```


```sh
docker-compose up -d 
```

## Installation Using Vagrant

<img src="https://github.com/susimsek/spring-boot-minio-example/blob/main/images/vagrant-installation.png" alt="Spring Boot Vagrant Installation" width="100%" height="100%"/> 

### Prerequisites

* Vagrant 2.2+
* Virtualbox or Hyperv

```sh
vagrant up
```

```sh
vagrant ssh
```

```sh
cd vagrant/setup
```

```sh
sudo chmod u+x *.sh
```

```sh
./install-prereqs.sh
```

```sh
exit
```

```sh
vagrant ssh
```

```sh
./mvnw compile jib:dockerBuild
```

```sh
docker-compose up -d
```

You can access the SpringDoc Openapi from the following url.

http://localhost:9090/api


## Used Technologies

* Spring Boot 2.3.8
* Minio  
* Spring Boot Web
* Spring Boot Validation
* Content Negotiation Support(Xml,Json,Yaml Support)
* Spring Boot Log4j2
* Problem Spring Web
* Spring Boot Actuator
* SpringDoc Openapi Web Mvc Core
* SpringDoc Openapi Web Ui
* Maven Jib Plugin
* Maven Clean Plugin
* Maven Enforcer Plugin
* Maven Compiler Plugin
* Lombok
* Dev Tools
* Spring Boot Test

## SpringDoc OpenApi

> You can access the SpringDoc Openapi from the following url.

http://localhost:9090/api

<img src="https://github.com/susimsek/spring-boot-minio-example/blob/main/images/springdoc-openapi.png" alt="SpringDoc Openapi" width="100%" height="100%"/> 

## Minio Browser

> You can access the Minio Browser from the following url.

http://localhost:9000

<img src="https://github.com/susimsek/spring-boot-minio-example/blob/main/images/minio-browser.png" alt="Minio Browser" width="100%" height="100%"/> 




