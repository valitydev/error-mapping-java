UTILS_PATH := build_utils
TEMPLATES_PATH := .

# Name of the service
SERVICE_NAME := geck
# Service image default tag
SERVICE_IMAGE_TAG ?= $(shell git rev-parse HEAD)
# The tag for service image to be pushed with
SERVICE_IMAGE_PUSH_TAG ?= $(SERVICE_IMAGE_TAG)

BUILD_IMAGE_TAG := 3ed839a79132205fd470c1f66c0b6aa37792c809

CALL_W_CONTAINER := clean all create java_compile compile doc deploy_nexus java_install
-include $(UTILS_PATH)/make_lib/utils_container.mk


ifdef SETTINGS_XML
DOCKER_RUN_OPTS = -v $(SETTINGS_XML):$(SETTINGS_XML)
DOCKER_RUN_OPTS += -e SETTINGS_XML=$(SETTINGS_XML)
endif

ifdef LOCAL_BUILD
DOCKER_RUN_OPTS += -v $$HOME/.m2:/home/$(UNAME)/.m2:rw
endif

java_compile:
	$(if $(SETTINGS_XML),,echo "SETTINGS_XML not defined" ; exit 1)
	mvn compile -s $(SETTINGS_XML)

deploy_nexus:
	$(if $(SETTINGS_XML),, echo "SETTINGS_XML not defined"; exit 1)
	mvn deploy -s $(SETTINGS_XML)

java_install:
	$(if $(SETTINGS_XML),, echo "SETTINGS_XML not defined"; exit 1)
	mvn install -s $(SETTINGS_XML)
