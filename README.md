url-shortener
=============

URL shortener service
DevLead & PM: Alexey Remnev
Participated: Oleg Razumov
Technologies: node.js, ruby, python, java, sql, mongo, redis, cassandra, mysql
Introduction
Implement REST-service that provides the URL shortening/expanding functionality like bit.ly
Functional requirements
REST interface
GET /shorten
Shorten specified URL.
Parameters:
url: URL-encoded long url
Example:
/shorten?url=https%3A%2F%2Fgoogle.ru%2Fsearch%3Fq%3Durl%2Bshortener
Effect: decode and store url into storage
Return code: 200 if OK, 50X on error

GET /
Expand shortened URL.
Path: if not equals “/shorten” - treat as a short URL
Effect: expand into long url from database, redirect to this URL
Return code: 301, Location=<longurl>
URL shortening algo
See this for reference, or any other proper shortening/expanded techniques.
Storage
Any storage that supports K/V data model.

Additional requirements:
should support sharding/clustering for HL/BD.
Web interface
Simple web-page that allows to construct shortening request.
Personalized history:
page to authenticate using Google
after authentication bind all shorten requests to the user
page showing list of all shortened urls by this user
Technical requirements
Implementation language (one of): java, python, ruby, node
Self contained unit test are must
Storage (one of): mysql, mongo, cassandra, redis
Web server (one of): nginx, apache

Packaging (one of): RPM or DEB file with all system dependencies + installation manual
Benchmarking tool: generate shorten and expand requests with urls length of 8-1000 characters e.g. using ab - calculate time spent for serving 10000 of such requests.

Time bounds
Up to 2 weeks for junior dev.

Resources
Redmine project: https://redmine.thumbtack.net/projects/url-shortener-service
Git repository: git@thumbtack.beanstalkapp.com:/url-shortener.git
create and use a separate branch named as your account
