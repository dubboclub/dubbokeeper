PROFILE?=mongodb
REGISTRY?=registry:5000/cloudecho
APP_VERSION?=latest
REPO?=dubbokeeper

default: package

package:
	mvn -U -DskipTests -P$(PROFILE) clean package assembly:assembly

image:
	docker build -t $(REGISTRY)/$(REPO):$(APP_VERSION) .

push: package image
	#cat ~/.docker/pass | docker login --username=cloudecho --password-stdin
	docker push $(REGISTRY)/$(REPO):$(APP_VERSION)
