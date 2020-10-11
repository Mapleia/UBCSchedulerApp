# Personal Project - UBC Course Scheduler

## OVERVIEW

UBC Course Scheduler is a Java application that will schedule your courses for you based on the timeslots of classes
that were scrapped for that academic year. **UBC Course Scheduler** will give a schedule that has all the inputted 
classes and try to best match your criteria. This will be limited to non-STT (**S**tandard **T**ime**t**able) students. Results will be a list of course 
sections that the student should take.

This is geared towards current undergrad students at the University of British Columbia, on the Vancouver campus. 
However, if given a properly formatted .csv sheet of the courses offered, this can be applied to more universities.

This project was of interest to me because of how tedious it was to manually make a schedule. I realized that my 
parameters for creating a schedule was fairly straight forward, and it could be sped up by automating it on a computer.
It can also be used for the rest of my degree, given that the course database gets updated every year.


## USER STORIES #1
- As a user, I want to add a course to my schedule
- As a user, I want to concentrate my courses in the PST morning, afternoon or evening
- As a user, I want to be able to choose whether to spread my classes over the entire week or concentrate them on a 
handful of days.
- As a user, I want to be able to have courses from both Winter Term 1 and Term 2, and have prerequisite courses from 
Term 1 come before Term 2. 