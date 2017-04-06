from django.db import models

STATUS_CHOICES = (
    ('Unapproved', 'Unapproved'),
    ('Approved', 'Approved'),
    ('Declined', 'Declined'),
)


class Attendant(models.Model):
    firstName = models.CharField(max_length=64, blank=False, default='')
    lastName = models.CharField(max_length=64, blank=True, default='')
    organization = models.CharField(max_length=64, blank=True, default='')
    email = models.CharField(max_length=64, blank=True, default='')
    phone = models.CharField(max_length=64, blank=True, default='')
    createTime = models.DateTimeField(auto_now_add=True)


class Location(models.Model):
    """
        Stores a location with name (building name), latitude and longitude.
    """
    name = models.CharField(max_length=64, blank=False)
    lat = models.FloatField(default=0.0)
    lng = models.FloatField(default=0.0)


class Admin(models.Model):
    """
        Stores an admin info with username (login name), password hash (sha256) and hash salt.
    """
    username = models.CharField(max_length=64, blank=False)
    pwdHash = models.CharField(max_length=64, blank=False)
    pwdSalt = models.CharField(max_length=16, blank=False)
    token = models.CharField(max_length=64, blank=True, default='')
    tokenExpirationTime = models.DateTimeField(blank=True, default='2000-01-01 00:00:00+00:00')


class User(models.Model):
    """
        Stores an applicant user info with access code hash (sha256), hash salt and personal info.
    """
    accessCodeHash = models.CharField(max_length=64, blank=False, default='')
    accessCodeSalt = models.CharField(max_length=16, blank=False, default='')
    firstName = models.CharField(max_length=64, blank=False, default='')
    lastName = models.CharField(max_length=64, blank=True, default='')
    organization = models.CharField(max_length=64, blank=True, default='')
    email = models.CharField(max_length=64, blank=True, default='')
    phone = models.CharField(max_length=64, blank=True, default='')
    token = models.CharField(max_length=64, blank=True, default='')
    tokenExpirationTime = models.DateTimeField(blank=True, default='2000-01-01 00:00:00+00:00')


class Announcement(models.Model):
    """
        Stores an announcement info with 4 parts:

        Part 1 - Basic info: Title, summary, detail and push date.

        Part 2 - Event specific info: isEvent boolean, location (stored id), location name/lat/lng (custom) and foodProvided boolean.

        (Part 2 is ignored if isEvent == false)

        Part 3 - Host info: Host's first name, last name, organization, email and phone.

        Part 4 - System info: Creation time (now), created by (a User object), current status (Unapproved, Approved, Declined, Pushed, Expired).

        Status will be auto-updated in a future version.
    """
    title = models.CharField(max_length=128, blank=True, default='')
    summary = models.CharField(max_length=4096, blank=True, default='')
    detail = models.CharField(max_length=65536, blank=True, default='')
    pushDate = models.DateField(blank=False, default='2000-01-01')

    isEvent = models.BooleanField(default=False)
    eventTime = models.DateTimeField(blank=True, default='2000-01-01 00:00:00+00:00')
    location = models.ForeignKey('Location', on_delete=models.SET_NULL, null=True)
    locationName = models.CharField(max_length=64, blank=True, default='')
    locationDesc = models.CharField(max_length=256, blank=True, default='')
    locationLat = models.FloatField(blank=True, default=0.0)
    locationLng = models.FloatField(blank=True, default=0.0)
    foodProvided = models.BooleanField(blank=True, default=False)

    hostFirstName = models.CharField(max_length=64, blank=False, default='')
    hostLastName = models.CharField(max_length=64, blank=True, default='')
    hostOrganization = models.CharField(max_length=64, blank=True, default='')
    hostEmail = models.CharField(max_length=64, blank=False, default='')
    hostPhone = models.CharField(max_length=64, blank=True, default='')

    createTime = models.DateTimeField(auto_now_add=True)
    createUser = models.ForeignKey('User', on_delete=models.SET_NULL, null=True)
    status = models.CharField(choices=STATUS_CHOICES, default='Unapproved', max_length=64)

    attendants = models.ManyToManyField('Attendant')
    
    cloudKey = models.CharField(max_length=64, blank=True, default='')

'''

class MultimediaAsset(models.Model):

    announcement = models.ForeignKey('Announcement', on_delete=models.CASCADE)
    key = models.CharField()
# Delete signal, asset cleanup
@receiver(pre_delete, sender=MultimediaAsset)
def multi_asset_delete(sender, instance, **kwargs):
    # delete cloud side asset
    pass

'''