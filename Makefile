PROFILE?=mongodb
REGISTRY?=registry:5000/cloudecho
APP_VERSION?=latest
DOCKER_PROJECTS?=dubbokeeper-ui dubbokeeper-server

default: package

package:
	mvn -U -DskipTests -P$(PROFILE) clean package

image:
	$(foreach P, $(DOCKER_PROJECTS), \
	docker build -t $(REGISTRY)/$P:$(APP_VERSION) $P ;)

push: package image
	$(foreach P, $(DOCKER_PROJECTS), \
	docker push $(REGISTRY)/$P:$(APP_VERSION) ;)
