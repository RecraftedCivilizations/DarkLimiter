# What does this plugin do?
DarkLimiter limits your access to certain things depending on your DarkCitizens
job/group. At the moment this plugin limits two things, first the access to doors, you can lock doors
for your self or for a group(depending on your perms). The other thing is the amount of blocks
you can place in total, the amount of blocks is defined by you admin.

## How to lock a door
To lock a door take out a stick and left-click the door you want to lock, after that
just call the `/lock` command, and the door will be locked for you.

## How to lock a door for a group
This will most likely only be possible for admins, but if you have the permissions use the `/lockForGroup <Groupname>` command.

## How to unlock a door
To unlock a door take out a stick again, select the door using left-click, and then call `/unlock`. All of your
doors will be unlocked automatically if you leave the server, only group doors are persistent.

## How to unlock a group door
Once again this will most likely only be possible for admins, but if you have the permissions use the `/unlockGroupDoor`

## How does the block limiting work
The block limiting will prevent you from placing more blocks if you are at the limit, if you or
someone else destroys the blocks you can place new ones. After you leave the server all blocks
you placed will be reset. You can reset your own blocks if you have the `dlm.reset` permission using
`/reset`

## For more info take a look at the ADMINDOCS