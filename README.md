1. Install Postgresql and run it.

2. In the file src/test/java/utils/DBUtil.java replace data for connection with your data.

In project we considers this database structure

<img width="350" alt="image" src="https://github.com/nromanen/speak_ukraine/assets/4123050/6086f17a-8346-475d-a768-3cc4f78e84e3">

Classes for models are specified in classes:
Category, Child, Club (in package model).

There are classes for interaction with the database:
CategoryDB, ChildDB, DBUtil (in package dao).

For methods of CategoryDB class there are exist tests in class CategoryDB (in package dao).

**Write tests for all public methods of the ChildDB class.**

For populate initial data in database you can use method executeFile from class utils.DBUtil.java

**After creating your tests in Actions you should have something like this picture.**
<img width="880" alt="image" src="https://github.com/taqc-java/jdbc/assets/61456363/53fe5bd6-d056-49a8-8442-9b08515dbae8">




