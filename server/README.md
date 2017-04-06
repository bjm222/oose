# Server Part:

## To run: 
To make sure we have updated the latest database relations, and configure the server properly. (Doesn't really matter at this stage, all objects are hardcoded for test)

## To Deploy:
The server is currently runable on a Ubuntu LTS 16.* version on a JHU CS department VM. Please note, we use PostgreSQL now for out back-end database support. You will have to set up a server for this.

### PostgreSQL Instrucation:
	sudo apt install postgresql
	sudo su - postgres
	psql
	CREATE USER aproot WITH PASSWORD weloveoose;
	CREATE DATABASE announcementplus;
	ALTER ROLE aproot SET client_encoding TO 'utf8';
	ALTER ROLE aproot SET default_transaction_isolation TO 'read committed';
	ALTER ROLE aproot SET timezone TO 'UTC';
	GRANT ALL PRIVILEGES ON DATABASE announcementplus TO aproot;
	ALTER USER aproot CREATEDB;
	\q
	exit

### Run:
	python manage.py makemigrations
	python manage.py migrate
	python manage.py runserver

### Test:
	coverage manage.py test
	coverage report Announcement/*.py

## Admin: 
You can add your self as an admin using, please don't push your version to the server. The oose account no longer exist because we don't use a file based database anymore. 

### Admin adding:

	python manage.py createsuperuser

## Endpoints: 
The server is currently on DEBUG model, meaning pages will include debug prints, and API end points will have an html wrapper, provided by the framework, instead of returning actual JSON.

	127.0.0.1:8000
is the default host for this server.
	
	/admin
will return Django admin page, where docs are generated.

## Requirements: 
	pip==9.0.1
	Django==1.10.2
	django-cors-headers==1.3.1
	django-extensions==1.7.4
	djangorestframework==3.4.7
	psycopg2==2.6.1
	whitenoise==3.2.2
	coverage==4.2