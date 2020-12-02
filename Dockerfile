FROM airhacks/glassfish

COPY ./target/book-store.war ${DEPLOYMENT_DIR}
