# Gegebot
<pre>
commands:
!event
purpose - create event
template:
!event {
   "title":"COGH1",
   "eventStartEpoch":1646406233,
   "recurTimeInSeconds":180,
   "roles":{
      "dps":{
         "count":1,
         "emoji":":angrydog:"
      }
   }
}

title - title of event
eventStartEpoch - start time of event in epoch https://www.epochconverter.com/
recurTimeInSeconds - 0 if no recur, x seconds for recur (example 1 day is 86400 seconds)
roles - roles of event
  count - count of roles
  emoji - emoji related to the role (discord custom emotes not handled yet)

!ping
purpose - to check if bot is up / alive

!delete
purpose - delete event manually
template: 
!delete 

-------------------------------------------------------------------------------------------
possible improvements
convert message to message embed
save model to data store
handling of custom emotes discord
</pre>
