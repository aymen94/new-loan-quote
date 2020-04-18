## instructions
 create image with Docker

```
 docker build -t loanquote .
```

after compiling, run image with command:
```
docker run -d  -p 8080:8080 loanquote
```

after followed above command, the web server is started on `http://localhost:8080`, and then login with:

* username: admin
* password: password 
