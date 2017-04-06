from rest_framework import serializers
from AnnouncementPlus.models import *
from django.utils import timezone


class DateTimeFieldWithTZ(serializers.DateTimeField):

    def to_representation(self, value):
        value = timezone.localtime(value)
        return super(DateTimeFieldWithTZ, self).to_representation(value)


class AttendantSerializer(serializers.ModelSerializer):
    """
    Serialize personal info part of a user for output only.
    """
    createTime = DateTimeFieldWithTZ(required=False, format="%Y-%m-%d %H:%M:%S")
    
    class Meta:
        model = Attendant
        fields = ('firstName', 'lastName', 'organization', 'email', 'phone', 'createTime')


class PersonalInfoSerializer(serializers.ModelSerializer):
    """
    Serialize personal info part of a user for output only.
    """
    class Meta:
        model = User
        fields = ('firstName', 'lastName', 'organization', 'email', 'phone')


class LocationSerializer(serializers.ModelSerializer):
    """
    Serialize a Location for input.
    """
    class Meta:
        model = Location
        fields = ('name', 'lat', 'lng')

class LocationGetSerializer(serializers.ModelSerializer):
    """
    Serialize a Location for output.
    """
    class Meta:
        model = Location
        fields = ('id', 'name', 'lat', 'lng')


class AdminSerializer(serializers.ModelSerializer):
    """
    Serialize an Admin for I/O.
    """
    class Meta:
        model = Admin
        fields = ('username', 'pwdHash', 'pwdSalt')


class UserSerializer(serializers.ModelSerializer):
    """
    Serialize a User for I/O.
    """
    class Meta:
        model = User
        fields = ('accessCodeHash', 'accessCodeSalt', 'firstName', 'lastName', 'organization', 'email', 'phone')

        # def create(self, validated_data):
        #     return User.objects.create(**validated_data)
        #
        # def update(self, instance, validated_data):
        #     instance.accCodeHash = validated_data.get('accessCodeHash', instance.accCodeHash)
        #     instance.accCodeSalt = validated_data.get('accCodeSalt', instance.accCodeSalt)
        #     instance.personalInfo = validated_data.get('personalInfo', instance.PersonalInfo)
        #     instance.save()
        #     return instance


class AnnouncementSerializer(serializers.ModelSerializer):
    """
    Serialize an Announcement for input only.
    """
    eventTime = serializers.DateTimeField(required=False, format="%Y-%m-%d %H:%M:%S")
    
    class Meta:
        model = Announcement
        fields = ('title', 'summary', 'detail', 'pushDate',
                  'isEvent', 'eventTime', 'location', 'locationName', 'locationDesc', 'locationLat', 'locationLng', 'foodProvided',
                  'hostFirstName', 'hostLastName', 'hostOrganization', 'hostEmail', 'hostPhone',
                  'createTime', 'createUser', 'status')


class AnnouncementGetSerializer(serializers.ModelSerializer):
    """
    Serialize an Announcement (without detail & system info) for output only.
    """
    eventTime = DateTimeFieldWithTZ(required=False, format="%Y-%m-%d %H:%M:%S")
    
    class Meta:
        model = Announcement
        fields = ('id', 'title', 'summary', 'pushDate',
                  'isEvent', 'eventTime', 'location', 'locationName', 'locationDesc', 'locationLat', 'locationLng', 'foodProvided',
                  'hostFirstName', 'hostLastName', 'hostOrganization', 'hostEmail', 'hostPhone')


class AnnouncementGetDetailSerializer(serializers.ModelSerializer):
    """
    Serialize an Announcement (with detail, without system info) for output only.
    """
    eventTime = DateTimeFieldWithTZ(required=False, format="%Y-%m-%d %H:%M:%S")
    
    class Meta:
        model = Announcement
        fields = ('id', 'title', 'summary', 'detail', 'pushDate',
                  'isEvent', 'eventTime', 'location', 'locationName', 'locationDesc', 'locationLat', 'locationLng', 'foodProvided',
                  'hostFirstName', 'hostLastName', 'hostOrganization', 'hostEmail', 'hostPhone',
                  'cloudKey')


class AnnouncementGetAttendantsSerializer(serializers.ModelSerializer):
    """
    Serialize attendants to an Announcement for output only.
    """
    attendants = AttendantSerializer(read_only=True, many=True)

    class Meta:
        model = Announcement
        fields = ('id', 'attendants')

class AnnouncementGetForManagementSerializer(serializers.ModelSerializer):
    """
    Serialize an Announcement (with system info, without detail) for output only.
    """
    eventTime = DateTimeFieldWithTZ(required=False, format="%Y-%m-%d %H:%M:%S")
    
    class Meta:
        model = Announcement
        fields = ('id', 'title', 'summary', 'pushDate',
                  'isEvent', 'eventTime', 'location', 'locationName', 'locationDesc', 'locationLat', 'locationLng', 'foodProvided',
                  'hostFirstName', 'hostLastName', 'hostOrganization', 'hostEmail', 'hostPhone',
                  'createTime', 'createUser', 'status')
