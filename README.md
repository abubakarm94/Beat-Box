# Beat Box

This App acts a back up service for your music files. It also lets your friends stream songs from your collection.


#HOW TO USE

1. Create a Parse application for Beat Box


2. In the login activity class, set up your parse id

Parse.initialize(this, "YOUR_APPLICATION_ID",
				"YOUR_CLIENT_KEY");
				
3. Set up your Facebook SDK, and create a facebook app at developer.facebook.com

4. in the login activity class input your facebook app id

ParseFacebookUtils.initialize(("YOUR FACEBOOK APP ID"));
		
		
# Features

Private Upload - Your friends can't see this song

Public Upload - Your Facebook friends can see and stream this song. 

#Potential improvement

Allow the user to upload multiple songs at a time rather than just one after the other

