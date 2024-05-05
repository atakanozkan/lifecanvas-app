COMP 319A FINAL PROJECT

LIFECANVAS APP

This is the submission repository of LIFECANVAS APP which is the final project of Android course in university.
The app contains register section, main section, note section , calendar and events section and sketch section. 

REGISTER SECTION

First when we run the app we are welcomed with Welcoming Screen.
As seen in figure below this is the welcoming screen.
<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 42 46" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/8dbdc097-be33-4b35-ba72-2273c19d20b5">
</p>

After we press start button we are directed to a register form with stepper.
There are three steps.

First step is where we enter our personal information; Name and Surname. This are used for users to personalize their application.
<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 45 12" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/30e32be8-3161-46a2-8d75-2bc082d46d18">
</p>

The second step is where we create our password for private notes. 
-Some notes can be public some can be private. 
- We added salt to hashing and we keep the salt and hashed password stored in save file.
- They are encrypted, so noone can read them in raw.
- This password is not asked everytime we run the app. It is only asked for private notes.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 47 49" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/08dafacc-25f4-444f-bdc2-6c9744eeda29">
</p>

The last step is the screen where we fill the checkbox if we agree the terms of the app.
- We cannot move further if we didn't fill the checkbox.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 49 00" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/1ebe5a17-3793-4382-bfe3-8ce0a63fef45">
</p>

MAIN SECTION

After filling the register form, we are welcomed with main screen. 
- If we have already defined user, we will be navigated to main screen.
- The main screen has 3 cards in its body. 
- These are the sections of the app. 
- Also, in top bar we have dark mode switch, welcome text and extra option button.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 51 43" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/07ea2a78-a0f2-4382-a906-4b2767fbf5a3">
</p>

If we click that more vert icon which is extra option icon button we have two type of actions.
- First one is "Change Password" and the other one is "Delete Account".
- When we press the drop down item as seen in the figure, a dialog panel is showed in screen for the related concept.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 53 34" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/e2e56687-abb2-496d-916a-11753c5e47f6">
</p>

If the dark switch mode is on, the app will look like in the figure. Also, we save this preference in user model. If we switch it to dark and quit the program. It will save this preference and it will open with dark mode for next time.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 55 57" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/fa09ad3e-dc3a-4658-abbb-6e83dc35715e">
</p>


NOTE SECTION:

In note section, 
- We have a search box with advanced filters. 
- We implemented this so user can search through the notes easliy. The body is scrollable.
- The notes are listed in card in body.
- Also we have floating button which is pressed in the figure below. We can add three types of notes.
- These notes can be private or public. If it is set to private we need to entered the password we set.
- -Othervwise we cannot edit or preview it.
- The notes are saved and updated in Room Db in notes table. 

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 19 59 37" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/37c030d9-cbda-4bda-ac7f-674ee620ffb1">
</p>

For example if we try to add text note, there will be dialog popped up in screen.
- The title cannot be used twiced. So, we need to choose unique one.
- Also we can set the mode of note.
- The title must be at least 3 letter and must start with a charachter.
  
<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 20 03 11" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/61bc401c-2086-4c55-b5e8-5b5996f7a043">
</p>

After we add note, we can edit it and it will be automatically saved.
There are also two icon buttons on right top corner for each note. The first one is editing title and privacy mode.
The other one is deleting the note.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 20 05 14" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/8b988a54-2cbc-46bf-8321-7e913a52af8a">
</p>


CALENDAR AND EVENTS SECTION

In calendar and events section, 
- We used calendar library of Java, Holiday Api from Rapid Api and other libraries of Java to time convert.
- We implemented a calendar view where we get the number of day in previewed month. 
- Then we put each day card in lazy vertcal grid. 
- Each card is clickable. If we click on the selected day, it will navigate to events detail of that day. 
- Also as seen in figure there are "+number" text inside card. It shows that there are holiday event or user event on that day. 
- In top bar we have navigate left and right icon buttons. Also add icon button.
- We can navigate through other months, with these iconbuttons.
- If we changed the year, we fetch the new year's holiday data from API. Then we put it on a model to list it on that day.
- The content is stored in Room Database as events table.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 20 07 08" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/d6108788-da0d-47a3-9b34-f16be5d3d1a2">
</p>

If we click on Add button, the add dialog appears with fields which are title, description, event start and end dates.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 20 15 41" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/c6099b00-96b8-42b9-997a-ce1c2f0df067">
</p>

When we go to a day in a calendar which has events for that day. We see the holidays and events in that day.
As you can see in the example we have holiday which are not editable and appears on the top and we have events that user created.
The events are editable and user can change the details of the event.
Also events are labeled as "Start" and "End" to show users easliy the start and end date of that event.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 20 17 30" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/674ea2c7-1fc4-4250-bef1-e8dc8a3c122f">
</p>

SKETCH SECTION

In sketch section, we also have the search box with advanced filters like we have in note section. 
The skecth models are listed in the body as card.
In left bottom of the screen, we have the add button for sketch canvas.
We are required to set the title which is also unique.
Then the skectch is created as bitmap and we can draw it.
The content is stored in Room Database as sketeches table.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 20 20 55" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/a1224bb4-30a2-48ed-8ff4-49ede748a0a7">
</p>

Each time we open the canvas, it will load the previous one in the screen. 
The sketeches we draw on the canvas are recorded as drawing path model. 
It has color, stroke and path attributes. 
If the sketch is not new, the old content is already loaded when we sketch anything. 
So, the new paths are added on the old content. 
Then we create a bitmap from the paths. 
After we create new bitmap and canvas, we draw the paths to save the bitmap. 
The bitmap is generated as a file in output stream.


In sketching canvas, we have features to change color and stroke of the drawing pen. 
Also, we have eraser to delete the paths on the canvas. 
In top bar, title is shown with navigate back button.
In left top bar, we have change title icon and delete icon button. 
In body we can draw anything we want.

<p align="center">
<img width="387" alt="Screen Shot 2024-01-16 at 20 30 40" src="https://github.com/atakan-ozkan/comp319a-2023f-final/assets/73070991/92269e28-b3a7-4f0b-af65-d73ffce5e9f2">
</p>

The sketches are autmatically saved when navigate back. The file path is also changed in the sketches table for the sketch data.



*** This is the brief description of the project ***


