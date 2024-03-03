FROM nginx:stable-alpine3.17-slim

COPY dist/apps/resource-catalog-management /apps/catalog
COPY dist/apps/party-management /apps/party
COPY dist/apps/agreement-management /apps/agreement
COPY dist/apps/resource-order-management /apps/order
COPY dist/apps/reporting-system /apps/reporting

#COPY dist/apps/resource-catalog-management/assets /apps/assets
#COPY dist/apps/party-management/assets /apps/assets
#COPY dist/apps/agreement-management/assets /apps/assets
#COPY dist/apps/resource-order-management/assets /apps/assets
COPY dist/apps/reporting-system/assets /apps/assets

RUN sed -i '1idaemon off;' /etc/nginx/nginx.conf

COPY ci/nginx.conf /etc/nginx/conf.d/default.conf

CMD ["nginx"]
