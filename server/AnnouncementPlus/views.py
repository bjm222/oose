# from django.http import Http404
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.renderers import JSONRenderer
from rest_framework.parsers import JSONParser
from django.core.exceptions import *
from django.core.mail import send_mail
import re

# from AnnouncementPlus.models import *
from AnnouncementPlus.serializers import *

from hashlib import sha256
import os
import codecs
import sys
import datetime
from django.utils.timezone import localtime, now
import logging

logger = logging.getLogger(__name__)



def auth(request):
    error = {}
    try:
        if 'HTTP_TOKEN' in request.META:
            token = request.META['HTTP_TOKEN']
            try: # pragma ignore
                user = User.objects.get(token=token) # pragma ignore
                if user.tokenExpirationTime > timezone.now(): # pragma ignore
                    result = {'type': 'user', 'id': user.id} # pragma ignore
                else: # pragma ignore
                    result = {'type': 'error', 'error': {'error': {'code': 401, 'message': 'Token expired'}}} # pragma ignore
                return result # pragma ignore

            except ObjectDoesNotExist:
                try:
                    admin = Admin.objects.get(token=token)
                    if admin.tokenExpirationTime > timezone.now():
                        result = {'type': 'admin', 'id': admin.id}
                    else:
                        result = {'type': 'error', 'error': {'error': {'code': 401, 'message': 'Token expired'}}} # pragma ignore
                    return result
                except ObjectDoesNotExist:
                    result = {'type': 'error', 'error': {'error': {'code': 401, 'message': 'Token not exist'}}}
        else:
            result = {'type': 'error', 'error': {'error': {'code': 401, 'message': 'No token in header'}}}
        return result
    except: # Unexpected error occurred
        result = {'type': 'error', 'error': {'error': {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}}}
        return result



class AnnouncementTodayView(APIView):
    """
        GET (arguments: none)

        Get today's pushed announcements (without detail).

        Return: List of multiple announcements

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            announcements = Announcement.objects.all().filter(status='Approved').filter(
                pushDate=timezone.now().date())
            serializer = AnnouncementGetSerializer(announcements, many=True)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

def createAttendant(data):
    attendant = Attendant.objects.create()
    # If field exists and not empty
    if 'firstName' in data and data['firstName']:
        attendant.firstName = data['firstName']
    if 'lastName' in data and data['lastName']:
        attendant.lastName = data['lastName']
    if 'organization' in data and data['organization']:
        attendant.organization = data['organization']
    if 'email' in data and data['email']:
        attendant.email = data['email']
    if 'phone' in data and data['phone']:
        attendant.phone = data['phone']
    attendant.save()
    return attendant

class AnnouncementAttendView(APIView):
    """
            POST (arguments: Announcement ID, Attendant First name, Last name, Organization, Email, Phone)

            Attend an event by personal info.

            Return: HTTP 201 Created

            .
        """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def post(self, request, id, format=None):
        error = {}
        try:
            announcement = Announcement.objects.get(id=id)
            # Check if the announcement is an event
            if announcement.isEvent:
                # Check if the event is approved
                if announcement.status == 'Approved':
                    # Check if the event has begun
                    now_time = timezone.now()
                    if announcement.eventTime > now_time:
                        if 'email' not in request.data or not request.data['email']:
                            attendant = createAttendant(request.data)
                        else:
                            try:
                                attendant = Attendant.objects.get(email=request.data['email'])
                                if 'firstName' in data and data['firstName']:
                                    attendant.firstName = data['firstName']
                                if 'lastName' in data and data['lastName']:
                                    attendant.lastName = data['lastName']
                                if 'organization' in data and data['organization']:
                                    attendant.organization = request.data['organization']
                                if 'phone' in data and data['phone']:
                                    attendant.phone = request.data['phone']
                                attendant.save()
                            except ObjectDoesNotExist:
                                attendant = createAttendant(request.data)

                        announcement.attendants.add(attendant)
                        announcement.save()
                        return Response({}, status=status.HTTP_201_CREATED)
                    else:
                        # print('The specified event has already begun.')
                        error['error'] = {'code': 400, 'message': 'The specified event has already begun.'}
                        return Response(error, status=status.HTTP_400_BAD_REQUEST)
                else:
                    # print('The specified event is not approved.')
                    error['error'] = {'code': 400, 'message': 'The specified event is not approved.'}
                    return Response(error, status=status.HTTP_400_BAD_REQUEST)
            else:
                # print('The specified announcement is not a event.')
                error['error'] = {'code': 400, 'message': 'The specified announcement is not a event.'}
                return Response(error, status=status.HTTP_400_BAD_REQUEST)

        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementBydateView(APIView):
    """
        GET (arguments: Date from, date to)

        Get announcements pushed/to be pushed in a specific date period.

        Return: List of multiple announcements.

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, datefrom, dateto, format=None):
        error = {}
        try:
            announcements = Announcement.objects.all().filter(status='Approved').filter(pushDate__range=[datefrom, dateto])
            serializer = AnnouncementGetSerializer(announcements, many=True)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementDetailView(APIView):
    """
        GET (arguments: Announcement ID)

        Get detail of a specific announcement.

        Return: One announcement with detail

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, id, format=None):
        error = {}
        try:
            announcement = Announcement.objects.get(id=id)
            serializer = AnnouncementGetDetailSerializer(announcement)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementManageView(APIView):
    """
        GET (arguments: none)

        Get announcements to be pushed in the future for management.

        Return: List of multiple announcements

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            announcements = Announcement.objects.all().filter(
                pushDate__range=[timezone.now().strftime('%Y-%m-%d'), '2099-12-31'])
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                user = User.objects.get(id=res_auth['id'])  # pragma ignore
                announcements = announcements.filter(hostEmail=user.email)  # pragma ignore
            serializer = AnnouncementGetForManagementSerializer(announcements, many=True)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementManageBydateView(APIView):
    """
        GET (arguments: none)

        Get announcements pushed/to be pushed in a specific date period, with management info.

        Return: List of multiple announcements

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, datefrom, dateto, format=None):
        error = {}
        try:
            announcements = Announcement.objects.all().filter(pushDate__range=[datefrom, dateto])
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                user = User.objects.get(id=res_auth['id']) # pragma ignore
                announcements = announcements.filter(hostEmail=user.email) # pragma ignore
            serializer = AnnouncementGetForManagementSerializer(announcements, many=True)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementGetAttendantsView(APIView):
    """
            GET (arguments: Announcement ID)

            Get attendants to an event.

            Return: List of attendants

            .
        """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, id, format=None):
        error = {}
        try:
            announcement = Announcement.objects.get(id=id)
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                user = User.objects.get(id=res_auth['id']) # pragma ignore
                if announcements.hostEmail != user.email: # pragma ignore
                    error['error'] = {'code': 403, 'message': 'Cannot view attendants of events submitted by others.'}  # pragma ignore
                    return Response(error, status=status.HTTP_403_FORBIDDEN)
            
            serializer = AnnouncementGetAttendantsSerializer(announcement)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
            
            
class AnnouncementManageApproveView(APIView):
    """
        PUT (arguments: id)

        Approve an announcement, change its status to 'Approved'.

        Return: HTTP 200

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def put(self, request, id, format=None):
        error = {}
        try:
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                error['error'] = {'code': 403, 'message': 'Only admins can make this request.'}
                return Response(error, status=status.HTTP_403_FORBIDDEN)
                
            try:
                announcement = Announcement.objects.get(id=id)
            except ObjectDoesNotExist:
                error['error'] = {'code': 404, 'message': 'Announcement does not exist'}
                return Response(error, status=status.HTTP_404_NOT_FOUND)
            announcement.status = 'Approved'
            announcement.save()
            return Response({}, status=status.HTTP_200_OK)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementManageDeclineView(APIView):
    """
        PUT (arguments: id)

        Decline an announcement, change its status to 'Declined'.

        Return: HTTP 200

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def put(self, request, id, format=None):
        error = {}
        try:
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                error['error'] = {'code': 403, 'message': 'Only admins can make this request.'}
                return Response(error, status=status.HTTP_403_FORBIDDEN)
            try:
                announcement = Announcement.objects.get(id=id)
            except ObjectDoesNotExist:
                error['error'] = {'code': 404, 'message': 'Announcement does not exist'}
                return Response(error, status=status.HTTP_404_NOT_FOUND)
            announcement.status = 'Declined'
            announcement.save()
            if re.match('[^@]+@[^@]+\.[^@]+', announcement.hostEmail) is not None:
                if 'reason' in request.data:
                    send_mail('Declined Announcement', 'Your announcement ' + announcement.title + ' has been declined. Reason: ' + request.data['reason'], 'noreply@announcementplus.com', [announcement.hostEmail])
                else:
                    send_mail('Declined Announcement', 'Your announcement ' + announcement.title + ' has been declined.', 'noreply@announcementplus.com', [announcement.hostEmail])
                            
            return Response({}, status=status.HTTP_200_OK)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementManageModifyView(APIView):
    """
        PUT (arguments: id)

        Decline an announcement, change its status to 'Declined'.

        Return: HTTP 200

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def put(self, request, id, format=None):
        error = {}
        isAdmin = True
        try:
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                isAdmin = False # pragma ignore
            try:
                announcement = Announcement.objects.get(id=id)
            except ObjectDoesNotExist:
                error['error'] = {'code': 404, 'message': 'Announcement does not exist'}
                return Response(error, status=status.HTTP_404_NOT_FOUND)

            # Part 1 - Basic info
            if 'title' in request.data:
                announcement.title = request.data['title']
            if 'summary' in request.data:
                announcement.summary = request.data['summary']
            if 'detail' in request.data:
                announcement.detail = request.data['detail']

            if 'pushDate' in request.data:
                # get current date
                now_time = timezone.now()
                # now_time = localtime(now())
                now_date = now_time.date()
                tomorrow = now_date + datetime.timedelta(days=1)
                push_date = datetime.datetime.strptime(request.data['pushDate'], '%Y-%m-%d').date()
                # if now_date < push_date:
                announcement.pushDate = push_date.strftime('%Y-%m-%d')
                # print(push_date.strftime('%Y-%m-%d'))
                # print(announcement['pushDate'])
                # else:
                # Push date is today or earlier
                # error['error'] = {'code': 400, 'message': 'Push date should be at least tomorrow: ' + tomorrow.strftime('%Y-%m-%d')}
                # return Response(error, status=status.HTTP_400_BAD_REQUEST)

            # Part 2 - Event specific info
            if request.data['isEvent'] == 'true' or request.data['isEvent'] == True:
                announcement.isEvent = True;
            else:
                announcement.isEvent = False;
            if announcement.isEvent == True:
                if 'eventTime' in request.data:
                    try:
                        push_date = datetime.datetime.strptime(request.data['pushDate'], '%Y-%m-%d')
                        event_time = datetime.datetime.strptime(request.data['eventTime'], '%Y-%m-%d %H:%M:%S')
                        push_tomorrow_date = push_date + datetime.timedelta(days=1)
                        if push_date < event_time and push_tomorrow_date > event_time:
                            announcement.eventTime = request.data['eventTime']
                        else:
                            # Event time is not in push date
                            error['error'] = {'code': 400,
                                              'message': 'Event time should be in push date: ' + push_date.strftime(
                                                  '%Y-%m-%d')}
                            return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    except ValueError:
                        error['error'] = {'code': 400, 'message': 'Wrong time format: ' + request.data['eventTime']}
                        return Response(error, status=status.HTTP_400_BAD_REQUEST)

                if 'foodProvided' in request.data:
                    if request.data['foodProvided'] == 'true' or request.data['foodProvided'] == True:
                        announcement.foodProvided = True;
                    else:
                        announcement.foodProvided = False;

                if 'location' in request.data:
                    # Uses a default location
                    loc_id = request.data['location']
                    try:
                        location = Location.objects.get(id=loc_id)
                        announcement.location = location
                        announcement.locationName = location.name
                        announcement.locationLat = location.lat
                        announcement.locationLng = location.lng
                    except ObjectDoesNotExist:
                        error['error'] = {'code': 400, 'message': 'Location not exist: ' + request.data['location']}
                        return Response(error, status=status.HTTP_400_BAD_REQUEST)
                else:
                    if 'locationName' in request.data and 'locationLat' in request.data and 'locationLng' in request.data:
                        # Uses custom location
                        announcement.location = None
                        announcement.locationName = request.data['locationName']
                        announcement.locationLat = request.data['locationLat']
                        announcement.locationLng = request.data['locationLng']

                # Add location description (e.g. room number) to announcement
                if 'locationDesc' in request.data:
                    announcement.locationDesc = request.data['locationDesc']

            # Part 3 - Host info
            if 'hostFirstName' in request.data:
                announcement.hostFirstName = request.data['hostFirstName']
            if 'hostLastName' in request.data:
                announcement.hostLastName = request.data['hostLastName']
            if 'hostOrganization' in request.data:
                announcement.hostOrganization = request.data['hostOrganization']
            if 'hostPhone' in request.data:
                announcement.hostPhone = request.data['hostPhone']

            # Part 4 - System info (createTime is auto generated)
            if isAdmin == False:
                announcement.status = 'Unapproved'
            
            # Part 5 - Cloud upload key
            if 'cloudKey' in request.data:
                announcement.cloudKey = request.data['cloudKey']

            # Storage and response
            announcement.save()
            #response = {}
            #response['message'] = 'Modify succeeded!'
            #return Response(response, status=status.HTTP_200_OK)
            return Response({}, status=status.HTTP_200_OK)
                
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementManageDeleteView(APIView):
    """
        DELETE (arguments: announcement's id)


        Return: HTTP 201.

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def delete(self, request, id, format=None):
        error = {}
        isAdmin = True
        try:
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                isAdmin = False # pragma ignore
                
            try:
                announcement = Announcement.objects.get(id=id)
            except ObjectDoesNotExist:
                error['error'] = {'code': 404, 'message': 'Announcement does not exist'}
                return Response(error, status=status.HTTP_404_NOT_FOUND)
                
            if isAdmin == False and announcement.status == 'Approved' and announcement.pushDate >= timezone.now().date():
                error['error'] = {'code': 403, 'message': 'Failed to delete: Your announcement has already been pushed.'}
                return Response(error, status=status.HTTP_403_FORBIDDEN)
            else:
                announcement.delete()
                return Response({}, status=status.HTTP_200_OK)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementSubmitView(APIView):
    """
        POST (arguments:

        title, summary, detail, pushDate,

        isEvent, location, locationName, locationDesc, locationLat, locationLng, foodProvided,

        hostFirstName, hostLastName, hostOrganization, hostEmail, hostPhone

        )

        Submit a new unapproved announcement.

        Event infos are ignored if the announcement is not an event.

        Event location can be assigned to a stored location (use location argument) or customized by applicant.

        If the applicant's email is not found in user database, a new user will be created and its access code will be generated.

        TODO: automatically send access code to applicant's email address (For debug purpose, currently access code is sent in response)

        Return: HTTP 201, DEBUG: access code

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def post(self, request, format=None):
        error = {}

        try:
            announcement = {}
            try:
                # Part 1 - Basic info
                if 'title' in request.data:
                    announcement['title'] = request.data['title']
                else:
                    error['error'] = {'code': 400, 'message': 'Title not found.'}
                    return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    
                if 'summary' in request.data:
                    announcement['summary'] = request.data['summary']
                else:
                    error['error'] = {'code': 400, 'message': 'Summary not found.'}
                    return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    
                if 'detail' in request.data:
                    announcement['detail'] = request.data['detail']
                else:
                    error['error'] = {'code': 400, 'message': 'Detail not found.'}
                    return Response(error, status=status.HTTP_400_BAD_REQUEST)

                if 'pushDate' in request.data:
                    try:
                        # get current date
                        now_time = timezone.now()
                        # now_time = localtime(now())
                        now_date = now_time.date()
                        tomorrow = now_date + datetime.timedelta(days=1)
                        push_date = datetime.datetime.strptime(request.data['pushDate'], '%Y-%m-%d').date()
                        # if now_date < push_date:
                        announcement['pushDate'] = push_date.strftime('%Y-%m-%d')
                        #   print(push_date.strftime('%Y-%m-%d'))
                        #   print(announcement['pushDate'])
                        # else:
                        #   Push date is today or earlier
                        #   error['error'] = {'code': 400, 'message': 'Push date should be at least tomorrow: ' + tomorrow.strftime('%Y-%m-%d')}
                        #   return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    except ValueError:
                        error['error'] = {'code': 400, 'message': 'Invalid push date.'}
                        return Response(error, status=status.HTTP_400_BAD_REQUEST)
                else:
                    error['error'] = {'code': 400, 'message': 'Push date not found.'}
                    return Response(error, status=status.HTTP_400_BAD_REQUEST)

                # Part 2 - Event specific info
                announcement['isEvent'] = request.data['isEvent']
                if announcement['isEvent'] == 'true' or announcement['isEvent'] == True:
                    try:
                        push_time = datetime.datetime.strptime(request.data['pushDate'], '%Y-%m-%d')
                        event_time = datetime.datetime.strptime(request.data['eventTime'], '%Y-%m-%d %H:%M:%S')
                        push_tomorrow_time = push_time + datetime.timedelta(days=1)
                        if push_time < event_time and push_tomorrow_time > event_time:
                            announcement['eventTime'] = request.data['eventTime']
                        else:
                            # Event time is not in push date
                            error['error'] = {'code': 400,
                                              'message': 'Event time should be in push date: ' + push_time.strftime(
                                                  '%Y-%m-%d')}
                            return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    except ValueError:
                        error['error'] = {'code': 400, 'message': 'Wrong time format: ' + request.data['eventTime']}
                        return Response(error, status=status.HTTP_400_BAD_REQUEST)

                    announcement['foodProvided'] = request.data['foodProvided']

                    if 'location' in request.data:
                        # Uses a default location
                        loc_id = request.data['location']
                        try:
                            location = Location.objects.get(id=loc_id)
                            announcement['location'] = location.id
                            announcement['locationName'] = location.name
                            announcement['locationLat'] = location.lat
                            announcement['locationLng'] = location.lng
                        except ObjectDoesNotExist:
                            error['error'] = {'code': 400, 'message': 'Location not exist: ' + request.data['location']}
                            return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    else:
                        # Uses custom location
                        announcement['location'] = None
                        announcement['locationName'] = request.data['locationName']
                        announcement['locationLat'] = request.data['locationLat']
                        announcement['locationLng'] = request.data['locationLng']

                    # Add location description (e.g. room number) to announcement
                    if 'locationDesc' in request.data:
                        announcement['locationDesc'] = request.data['locationDesc']

                # Part 3 - Host info & user creation
                
                # fill personal info
                if 'hostFirstName' in request.data:
                    announcement['hostFirstName'] = request.data['hostFirstName']
                else:
                    error['error'] = {'code': 400, 'message': 'Host first name not found.'}
                    return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    
                if 'hostLastName' in request.data:
                    announcement['hostLastName'] = request.data['hostLastName']
                    
                if 'hostOrganization' in request.data:
                    announcement['hostOrganization'] = request.data['hostOrganization']

                if 'hostEmail' in request.data:
                    announcement['hostEmail'] = request.data['hostEmail']
                else:
                    error['error'] = {'code': 400, 'message': 'Host email not found.'}
                    return Response(error, status=status.HTTP_400_BAD_REQUEST)
                    
                if 'hostPhone' in request.data:
                    announcement['hostPhone'] = request.data['hostPhone']
                
                # Create new user if email not in database
                is_new_user = False
                access_code = ''
                try:
                    user = User.objects.get(email=request.data['hostEmail'])
                    user.firstName = request.data['hostFirstName']
                    user.lastName = request.data['hostLastName']
                    user.organization = request.data['hostOrganization']
                    user.phone = request.data['hostPhone']
                    user.save()
                    announcement['createUser'] = user.id
                except ObjectDoesNotExist:
                    # need to create new user
                    new_user = User.objects.create()
                    is_new_user = True
                    access_code = codecs.encode(os.urandom(8), 'hex').decode()
                    new_user.accessCodeSalt = codecs.encode(os.urandom(8), 'hex').decode()
                    new_user.accessCodeHash = sha256((access_code + new_user.accessCodeSalt).encode('utf-8')).hexdigest()
                    new_user.firstName = request.data['hostFirstName']
                    new_user.lastName = request.data['hostLastName']
                    new_user.organization = request.data['hostOrganization']
                    new_user.email = request.data['hostEmail']
                    new_user.phone = request.data['hostPhone']
                    new_user.save()
                    announcement['createUser'] = new_user.id

                # Part 4 - System info (createTime is auto generated)
                announcement['status'] = 'Unapproved'
                
                # Part 5 - Cloud upload key
                if 'cloudKey' in request.data:
                    announcement['cloudKey'] = request.data['cloudKey']

                # Serialization, storage and response
                serializer = AnnouncementSerializer(data=announcement)

                if serializer.is_valid():
                    serializer.save()
                    if is_new_user:
                        # resp_data = {}
                        # resp_data['accessCode'] = access_code
                        send_mail('Your access code', 'Please use your email ' + announcement['hostEmail'] + ' and access code ' + access_code + ' to manage your announcements.', 'noreply@announcementplus.com', [announcement['hostEmail']])
                
                        # return Response(data=resp_data, status=status.HTTP_201_CREATED)
                    # else:
                    return Response({}, status=status.HTTP_201_CREATED)

                else:
                    print(serializer.errors)
                    error['error'] = {'code': 500, 'message': 'Data serialization failed'}
                    return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

            except KeyError as e:
                # Some item in request is not found
                error['error'] = {'code': 400, 'message': 'Item not found: ' + str(e)}
                return Response(error, status=status.HTTP_400_BAD_REQUEST)

        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AdminLoginView(APIView):
    """
        POST (arguments: username, password)

        Login to system as admin. Password is hashed with salt and compared with stored password hash.

        Return: HTTP 200

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def post(self, request, format=None):
        error = {}
        try:
            # Validations
            if 'username' not in request.data:
                error['error'] = {'code': 422, 'message': 'Invalid username'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)
            if 'password' not in request.data:
                error['error'] = {'code': 422, 'message': 'Invalid password'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)

            # Find username in database
            try:
                admin = Admin.objects.get(username=request.data['username'])
            except ObjectDoesNotExist:
                error['error'] = {'code': 401, 'message': 'Wrong login credentials'}
                return Response(error, status=status.HTTP_401_UNAUTHORIZED)

            # Match hash to confirm password
            salt = admin.pwdSalt
            new_hash = sha256((request.data['password'] + salt).encode('utf-8')).hexdigest()
            if new_hash == admin.pwdHash:
                token = codecs.encode(os.urandom(8), 'hex').decode()
                admin.token = token
                admin.tokenExpirationTime = timezone.now() + datetime.timedelta(hours=1)
                admin.save()
                resp_data = {}
                resp_data['token'] = token
                response = Response(data=resp_data, status=status.HTTP_200_OK)
                return response
            else:
                error['error'] = {'code': 401, 'message': 'Wrong login credentials'}
                return Response(error, status=status.HTTP_401_UNAUTHORIZED)

        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AdminRegisterView(APIView):
    """
        POST (arguments: username, password)

        Register as admin. Username must be 5-20 characters and password must be 6-30 characters.

        Password is hashed with salt and then stored in database.

        Return: HTTP 201

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def post(self, request, format=None):
        error = {}

        try:
            # Validations
            if 'username' not in request.data or len(request.data['username']) < 5 or len(
                    request.data['username']) > 20:
                error['error'] = {'code': 422, 'message': 'Invalid username (please use 5-20 characters)'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)
            if 'password' not in request.data or len(request.data['password']) < 6 or len(
                    request.data['password']) > 30:
                error['error'] = {'code': 422, 'message': 'Invalid password (please use 6-30 characters)'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)

            # Create admin; generate new password hash and salt
            admin = {}
            admin['username'] = request.data['username']
            admin['pwdSalt'] = codecs.encode(os.urandom(8), 'hex').decode()
            admin['pwdHash'] = sha256((request.data['password'] + admin['pwdSalt']).encode('utf-8')).hexdigest()

            serializer = AdminSerializer(data=admin)
            if serializer.is_valid():
                serializer.save()
                return Response({}, status=status.HTTP_201_CREATED)

        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AdminChangepwdView(APIView):
    """
        POST (arguments: username, password, new password)

        Change an admin's password if username and old password matches. New password will be hashed with new salt.

        Return: HTTP 200

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def post(self, request, format=None):
        error = {}

        try:
            # Validations
            if 'username' not in request.data:
                error['error'] = {'code': 422, 'message': 'Username cannot be empty'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)
            if 'new_pwd' not in request.data or len(request.data['new_pwd']) < 6 or len(request.data['new_pwd']) > 30:
                error['error'] = {'code': 422, 'message': 'Invalid new password (please use 6-30 characters)'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)

            # Find username in database
            try:
                admin = Admin.objects.get(username=request.data['username'])
            except ObjectDoesNotExist:
                error['error'] = {'code': 401, 'message': 'Wrong admin username'}
                return Response(error, status=status.HTTP_401_UNAUTHORIZED)

            # Generate new password hash and salt
            admin.pwdSalt = codecs.encode(os.urandom(8), 'hex').decode()
            admin.pwdHash = sha256((request.data['new_pwd'] + admin.pwdSalt).encode('utf-8')).hexdigest()
            admin.save()
            return Response({}, status=status.HTTP_200_OK)

        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

#
# class UserDetailView(APIView):
#     """
#         GET (arguments: user id)
#
#         Get a specific user's personal info.
#
#         Return: The user's personal info.
#
#         .
#     """
#     parser_classes = (JSONParser,)
#     renderer_classes = (JSONRenderer,)
#
#     def get(self, request, id, format=None):
#         error = {}
#         try:
#             user = User.objects.get(id=id)
#             serializer = PersonalInfoSerializer(user)
#             return Response(serializer.data)
#         except:#  # Unexpected error occurred
#             # error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
#             return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class UserAccessView(APIView):
    """
            POST (arguments: email, access code)

            Access management page as applicant. Access code is hashed with salt and compared with stored password hash.

            Return: HTTP 200

            .
        """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def post(self, request, format=None):
        error = {}
        try:
            # Validations
            if 'email' not in request.data:
                error['error'] = {'code': 422, 'message': 'Email cannot be empty'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)
            if 'accessCode' not in request.data:
                error['error'] = {'code': 422, 'message': 'Access code cannot be empty'}
                return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)

            # Find username in database
            try:
                user = User.objects.get(email=request.data['email'])
            except ObjectDoesNotExist:
                error['error'] = {'code': 401, 'message': 'Invalid email'}
                return Response(error, status=status.HTTP_401_UNAUTHORIZED)

            # Match hash to confirm password
            salt = user.accessCodeSalt
            new_hash = sha256((request.data['accessCode'] + salt).encode('utf-8')).hexdigest()
            if new_hash == user.accessCodeHash:
                user.token = codecs.encode(os.urandom(8), 'hex').decode()
                user.tokenExpirationTime = timezone.now() + datetime.timedelta(hours=1)
                user.save()
                resp_data = {"token": token}
                return Response(data=resp_data, status=status.HTTP_200_OK)
            else:
                error['error'] = {'code': 401, 'message': 'Invalid access code'}
                return Response(error, status=status.HTTP_401_UNAUTHORIZED)

        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class UserRetrieveCodeView(APIView):
    def post(self, request, format=None):
        error = {}
        # Error checking for invalid request
        if 'email' not in request.data:
            error['error'] = {'code': 422, 'message': 'Email cannot be empty'}
            return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)

        if re.match('[^@]+@[^@]+\.[^@]+', request.data['email']) is None:
            error['error'] = {'code': 422, 'message': 'Wrong email format'}
            return Response(error, status=status.HTTP_422_UNPROCESSABLE_ENTITY)

        try:
            user = User.objects.get(email=request.data['email'])
        except ObjectDoesNotExist:
            error['error'] = {'code': 404, 'message': 'Invalid email'}
            return Response(error, status=status.HTTP_404_NOT_FOUND)

        re.match('[^@]+@[^@]+\.[^@]+', request.data['email'])

        access_code = codecs.encode(os.urandom(8), 'hex').decode()
        user.accessCodeSalt = codecs.encode(os.urandom(8), 'hex').decode()
        user.accessCodeHash = sha256((access_code + user.accessCodeSalt).encode('utf-8')).hexdigest()
        user.save()

        send_mail('Your new access code', 'Your access code has been reset. New access code: ' + access_code, 'noreply@announcementplus.com', [request.data['email']])

        return Response({}, status=status.HTTP_200_OK)


class LocationListView(APIView):
    """
        GET (arguments: none)

        Get a list of all stored locations.

        Return: List of all stored locations.

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            locations = Location.objects.all().filter()
            serializer = LocationGetSerializer(locations, many=True)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class LocationAddView(APIView):
    """
        POST (arguments: new location's name, lat, lng)

        Create a new location and store it into database.

        Return: HTTP 201.

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def post(self, request, format=None):
        error = {}
        # Serialization, storage and response
        # print(request.data)
        
        res_auth = auth(request)
        if res_auth['type'] == 'error':
            return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
        elif res_auth['type'] == 'user':
            error['error'] = {'code': 403, 'message': 'Only admins can make this request.'}
            return Response(error, status=status.HTTP_403_FORBIDDEN)
        
        serializer = LocationSerializer(data=request.data)

        if serializer.is_valid():
            serializer.save()
            return Response({}, status=status.HTTP_201_CREATED)

        else:
            return Response(error, status=status.HTTP_400_BAD_REQUEST)


class LocationDeleteView(APIView):
    """
        DELETE (arguments: location's id)

        Delete a location.

        TODO: Need admin authentication

        Return: HTTP 201.

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def delete(self, request, id, format=None):
        error = {}
        try:        
            res_auth = auth(request)
            if res_auth['type'] == 'error':
                return Response(res_auth['error'], status=status.HTTP_401_UNAUTHORIZED)
            elif res_auth['type'] == 'user':
                error['error'] = {'code': 403, 'message': 'Only admins can make this request.'}
                return Response(error, status=status.HTTP_403_FORBIDDEN)
                
            try:
                location = Location.objects.get(id=id)
            except ObjectDoesNotExist:
                error['error'] = {'code': 404, 'message': 'Announcement does not exist'}
                return Response(error, status=status.HTTP_404_NOT_FOUND)
            location.delete()
            return Response({}, status=status.HTTP_200_OK)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

            

class GetUploadInfoView(APIView):
    """
        GET (arguments: none)

        Get upload info for images.

        Return: accessKeyId, secretAccessKey, bucket, key

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            message = {}
            message['accessKeyId'] = 'AKIAJ7LZ27DKCDCUA6ZA'
            message['secretAccessKey'] = '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'
            message['bucket'] = 'announcementplus'
            message['key'] = codecs.encode(os.urandom(4), 'hex').decode()
            return Response(message, status=status.HTTP_200_OK)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
            

class GetAttendantCountView(APIView):
    """
        GET (arguments: none)

        Get attendant count.

        Return: count

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            message = {}
            message['count'] = Attendant.objects.count()
            return Response(message, status=status.HTTP_200_OK)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
            
            
# DEBUG API

class AnnouncementAllView(APIView):
    """
        GET (arguments: none)

        DEBUG: Get all announcements stored in database.

        Return: List of multiple announcements

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            announcements = Announcement.objects.all().filter()
            serializer = AnnouncementGetSerializer(announcements, many=True)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AnnouncementManageAllView(APIView):
    """
        GET (arguments: none)

        DEBUG: Get all announcements stored in database, with management info.

        TODO: add authentication for admin & applicant

        Return: List of multiple announcements

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            announcements = Announcement.objects.all().filter()
            serializer = AnnouncementGetForManagementSerializer(announcements, many=True)
            return Response(serializer.data)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


'''
class AnnouncementManageUnapproveView(APIView):
    """
        PUT (arguments: id)

        DEBUG: Unapprove an announcement, change its status to 'Unapproved'.

        Return: HTTP 200

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def put(self, request, id, format=None):
        error = {}
        try:
            try:
                announcement = Announcement.objects.get(id=id)
            except ObjectDoesNotExist:
                error['error'] = {'code': 404, 'message': 'Announcement does not exist'}
                return Response(error, status=status.HTTP_404_NOT_FOUND)
            announcement.status = 'Unapproved'
            announcement.save()
            return Response({}, status=status.HTTP_200_OK)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
'''

'''
class TokenTestView(APIView):
    """
        GET (arguments: none)

        DEBUG: Get all announcements stored in database, with management info.

        Return: List of multiple announcements

        .
    """
    parser_classes = (JSONParser,)
    renderer_classes = (JSONRenderer,)

    def get(self, request, format=None):
        error = {}
        try:
            if 'HTTP_TOKEN' in request.META:
                token = request.META['HTTP_TOKEN']
                try:
                    user = User.objects.get(token=token)
                    if user.tokenExpirationTime > timezone.now():
                        message = {'message': 'You have an User token with ID ' + str(user.id)}
                        return Response(message, status=status.HTTP_200_OK)
                    else:
                        error['error'] = {'code': 401, 'message': 'User token expired'}
                        return Response(error, status=status.HTTP_401_UNAUTHORIZED)

                except ObjectDoesNotExist:
                    try:
                        admin = Admin.objects.get(token=token)
                        if admin.tokenExpirationTime > timezone.now():
                            message = {'message': 'You have an Admin token with ID ' + str(admin.id)}
                            return Response(message, status=status.HTTP_200_OK)
                        else:
                            error['error'] = {'code': 401, 'message': 'Admin token expired'}
                            return Response(error, status=status.HTTP_401_UNAUTHORIZED)
                    except ObjectDoesNotExist:
                        error['error'] = {'code': 401, 'message': 'Token not exist'}
                        return Response(error, status=status.HTTP_401_UNAUTHORIZED)
            else:
                error['error'] = {'code': 401, 'message': 'No token in header'}
                return Response(error, status=status.HTTP_401_UNAUTHORIZED)
        except: # Unexpected error occurred
            error['error'] = {'code': 500, 'message': 'Unexpected error: ' + sys.exc_info()[0].__name__}
            return Response(error, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

'''