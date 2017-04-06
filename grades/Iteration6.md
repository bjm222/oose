#### Group Number: 8 Todays Announcement+

#### Presentation

Generally good.

##### App demo comments.  Did any errors/glitches arise during the demo?

* Demo went very well.

* It’s nice that the application doesn’t require signing up with username and password. Instead, it generates access tokens.

* Doing dates in a good format (ISO).

* Secured administrative user creation page by restricting the request origin. Clever way of dodging having an authentication system.

* Three front-ends: one of people posting announcements (web), two for attendees (web and Android).

##### UI quality -- look and usability

* Very good of various js tools.  Highlight selected items (minor glitch)

* Phone app also looks good.

#### Iteration 6

##### Code inspection

Code review included deeply nested if/then/else try/catch blocks. Not good style.  In general your code looks quite hackish, few files few methods and in major need of extract method etc refactoring.

##### Architecture - was the code cleanly structured in terms of packages, deployment, etc

Very good.

##### Tests - good coverage?  Travis working?

##### Final code documentation

##### GitHub usage

#### Project difficulty in terms of lines of code, conceptual difficulty, non-CRUD features, degree of completion

Your non-CRUD feature is small. 

1400 lines of server code and 2000+ lines of Android code which is reasonable, plus all the JavaScript etc on top.

#### Overall remarks

A solid project, with generally good architecture and features but work on code quality is needed.

**Grade: 92/100**
