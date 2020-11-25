# Personal Project - UBC Course Scheduler

## OVERVIEW

UBC Course Scheduler is a Java application that will schedule your courses for you based on the timeslots of classes
that were scrapped for that academic year. **UBC Course Scheduler** will give a schedule that has all the inputted 
classes and try to best match your criteria. This will be limited to non-STT (**S**tandard **T**ime**t**able) and 
undergraduate students. Results will be a list of course sections that the student should take. 

This is geared towards current undergrad students at the University of British Columbia, on the Vancouver campus. 
However, if given a properly formatted .json sheet of the courses offered, this can be applied to more universities.

If you'd like to use it for your own university courses, please look at the sample JSON data and format your own set of 
data. 

## MOTIVATION
This project was of interest to me because of how tedious it was to manually make a schedule. I realized that my 
parameters for creating a schedule was fairly straight forward, and it could be sped up by automating it.
It can also be used for the rest of my degree, given that the course database gets updated every year.


## USER STORIES 1
- As a user, I want to have multiple courses in my schedule.
- As a user, I want to concentrate my courses in the PST morning, afternoon or evening.
- As a user, I want to see the final list of sections that I need to register for in my schedule.
- As a user, I want to remove some courses and add others. 

## USER STORIES 2
- As a user, I want to load up a previous session and keep working on it.
- As a user, I want to download/save the schedule made by the program and retrieve it.
- As a user, I want to print out my schedule in the console, so I can read it.

## CREDITS
Data found by downloading from https://ubc-courses-api.herokuapp.com/. 

## DISCLAIMER
*Pulled from scrapper in November 2020. Course schedule may have changed.*

*No UBC 2021 summer courses added to the database yet.*