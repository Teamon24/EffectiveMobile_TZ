ARG ELK_VERSION
ARG ELK_IMAGE_PATH

FROM ${ELK_IMAGE_PATH}kibana:${ELK_VERSION}

# Search Guard plugin
ARG ELK_VERSION
ARG SG_VERSION_KIBANA
RUN kibana-plugin install https://maven.search-guard.com/search-guard-kibana-plugin-release/com/floragunn/search-guard-kibana-plugin/${ELK_VERSION}-${SG_VERSION_KIBANA}/search-guard-kibana-plugin-${ELK_VERSION}-${SG_VERSION_KIBANA}.zip

# Add your kibana plugins setup here
# Example: RUN kibana-plugin install <name|url>
