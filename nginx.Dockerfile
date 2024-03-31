FROM nginx:alpine

WORKDIR /etc/nginx
#COPY data/nginx/nginx.conf ./conf.d/default.conf
EXPOSE 80
ENTRYPOINT [ "nginx" ]
CMD [ "-g", "daemon off;" ]
