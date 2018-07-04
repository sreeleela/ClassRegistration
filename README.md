# ClassRegistration
Android app for university students to register classes and manage them. Web services are consumed.
# App Description
The app will allow users to register for classes. A student needs to enter their personal information: first name, last name, red id, email address and a password. The user can register for up to three courses and add themselves to waitlists for courses that are full. The student can also drop classes that they are registered for or drop themselves from waitlists. The app will allow students to select from a list of courses filtered via major (or subject like Computer Science, Physics, etc), level of the course (lower division - 100 & 200 level courses, upper division courses 300, 400, 500 level courses, and graduate courses (500 level and higher), and time of day (classes starting after a given time and/or ending before a given time). The server has a list of over 4,000 courses so we do not want to display all courses in one list. Student can filter classes based on subject(s), time of data, and level. The app display's the courses the student is enrolled in and the courses that they are waitlisted for. The app stores the students personal data on the device so that they do not have to enter the data each time they use the app.
# Server Interaction Overview
A brief overview of the commands that are send to the server (web services consumption).<br>
  * subjectlist Returns a list of majors or subjects. Includes the name of the subject, the college 
  it is in allowing the classes to be grouped by college.
  * classidslist Returns a list of courses based on subject(s), level, and time. Returns just the ids of the courses.
  * classdetails Given a course id returns information about the course: title, instructor, meeting time and place, etc.
  * addstudent Given the personal information about the student added the student to the server so they can add classes.
  * registerclass Given a course id, student’s red id and password registers the student in the class.
  * waitlistclass If a class is full given a course id, student’s red id and password adds the student to the wait list of a class. If a  student drops the course the server does not enroll a
  student from the waitlist in the course.
  * unregisterclass Drops a student from the course.
  * unwaitlistclass Removes the student from a course waitlist.
  * resetstudent Drops the student from all courses and removes them from all waitlists.
  # How to run this project
1. open android studio<br>
   File -> New -> Project from Version Control -> GitHub (Url: https://github.com/sreeleela/ClassRegistration.git )(clone from here)<br>
   Now Run the Project
   
2. Download Zip file of Project and unzip it (Suppose the unziped folder is in downloads and the name is ClassRegistration-master)<br>
   open android studio<br>
   File -> New -> Import Project<br>
   Select Eclipse or Gradle Project to Import<br>
   Downloads -> ClassRegistration-master -> ClassRegistration-master<br>
   Now run the project<br>
   
<br>If the build fails do the following<br>
Tools -> Android -> Syn Project with Gradle Files<br>
And run the project again
