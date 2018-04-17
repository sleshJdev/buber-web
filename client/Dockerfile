FROM node:latest
RUN mkdir -p /home/gallery
WORKDIR /home/gallery
COPY . .
RUN npm install
EXPOSE 7333
ENTRYPOINT [ "npm", "run", "dev" ]
