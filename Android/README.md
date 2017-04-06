# Android Part:
We finish the implementation of App's architecure. 

* The min SDK version the App support is 18, and target SDK version is 24.

* Through the navigation drawer, user can go to three different fragments: map fragment, list fragment and setting fragment.

* In the map fragment(events near me) in order to show map correctly you may need to follow the instruction in https://developers.google.com/maps/documentation/android/start#get-key to apply a new API key and replace current API key in google_maps_api.xml file. Otherwise, you may see a blank map with a Google logo on the left corner.

* All Announcement, Up Coming Events and History are all implemented as list fragment. In the list fragment user can see summaries of all events, user can also view detail information of a event by pressing element inside the list.

* In the setting fragment(Setting) user can enter his/her personal information.

