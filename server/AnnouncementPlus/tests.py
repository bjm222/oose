from rest_framework.test import APIRequestFactory

# Using the standard RequestFactory API to create a form POST request
factory = APIRequestFactory()

from django.test import TestCase
from rest_framework.test import APITestCase
from AnnouncementPlus.models import *
from AnnouncementPlus.views import *
import datetime
from django.utils.timezone import localtime, now

class LocationTestCases(APITestCase):
    def test_listCreateDelete(self):
        # User register
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # User login
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)
        token = response.data['token']

        # List location (empty)
        response = self.client.get('/location/list')
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data, [])
        
        # Add location (unauthorized)
        response = self.client.post('/location/add', JSONRenderer().render(
            {'name': 'Malone Hall', 'lat': float(34.777), 'lng': float(-118.030)}),
                                         content_type='application/json')
        self.assertEqual(response.status_code, 401)
        
        # Add location (wrong content)
        response = self.client.post('/location/add', JSONRenderer().render(
            {'name': 'Malone Hall', 'lat': float(34.777), 'lng': float(-118.030)}),
                                         content_type='multipart/mixed', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 415)

        response = self.client.post('/location/add', JSONRenderer().render(
            {}),
                                         content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 400)
        
        # Add location
        response = self.client.post('/location/add', JSONRenderer().render(
            {'name': 'Malone Hall', 'lat': float(34.777), 'lng': float(-118.030)}),
                                         content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 201)

        # List location
        response = self.client.get('/location/list')
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['name'], 'Malone Hall')
        self.assertEqual(response.data[0]['lat'], 34.777)
        self.assertEqual(response.data[0]['lng'], -118.03)
        loc_id = response.data[0]['id']
        
        # Submit event with default location (location not exist)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Event", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": "2016-12-25",
                "isEvent": "true", "eventTime": "2016-12-25 12:00:00", "location": str(233), "locationDesc": "Room 101", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostEmail": "hostthree@organization3.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit event with default location
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Event", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": "2016-12-25",
                "isEvent": "true", "eventTime": "2016-12-25 12:00:00", "location": str(loc_id), "locationDesc": "Room 101", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostEmail": "hostthree@organization3.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # Add another location
        response = self.client.post('/location/add', JSONRenderer().render(
            {'name': 'Charles Commons', 'lat': float(37.030), 'lng': float(-118.033)}),
                                         content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 201)
        
        # List location
        response = self.client.get('/location/list')
        self.assertEqual(len(response.data), 2)
        for item in response.data:
            if item['name'] == 'Charles Commons':
                loc2_id = item['id']        
        
        # Get submitted event
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 1)
        announcement_id = response.data[0]['id']
        
        # Modify submitted event to another location (location not exist)
        response = self.client.put('/announcement/manage/modify/' + str(announcement_id), JSONRenderer().render(
            {
                "title": "Modified Test Submit Event", "summary": "Modified Test Submit Summary", "detail": "Modified Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "true", "eventTime": "2017-01-01 18:00:00", "location": str(233), "locationDesc": "Room 202", "foodProvided": "true",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostPhone": "4445556666"
            }),
            content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 400)
        
        # Modify submitted event to another location
        response = self.client.put('/announcement/manage/modify/' + str(announcement_id), JSONRenderer().render(
            {
                "title": "Modified Test Submit Event", "summary": "Modified Test Submit Summary", "detail": "Modified Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "true", "eventTime": "2017-01-01 18:00:00", "location": str(loc2_id), "locationDesc": "Room 202", "foodProvided": "true",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostPhone": "4445556666"
            }),
            content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        # Delete nonexistent location
        response = self.client.delete('/location/delete/' + str(233), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 404)
        
        # Delete location (unauthorized)
        response = self.client.delete('/location/delete/' + str(loc_id))
        self.assertEqual(response.status_code, 401)
        
        # Delete location
        response = self.client.get('/location/list')
        delete_id = response.data[0]['id']
        response = self.client.delete('/location/delete/' + str(loc_id), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        # List location
        response = self.client.get('/location/list')
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 1)
        
        # Delete location
        response = self.client.get('/location/list')
        delete_id = response.data[0]['id']
        response = self.client.delete('/location/delete/' + str(loc2_id), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        # List location (empty)
        response = self.client.get('/location/list')
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data, [])
        
class AnnouncementTestCases(APITestCase):

    def test_byDate(self):
        # User register
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # User login
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)
        token = response.data['token']

        # Test empty announcements
        response = self.client.get('/announcement/bydate/2011-01-01/2017-01-02')
        self.assertEqual(response.data, [])

        # Submit announcement
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Announcement", "summary": "Test Summary 1", "detail": "Test Detail 1", "pushDate": "2017-01-01",
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)            
            
        # Get Submitted Announcement
        response = self.client.get('/announcement/manage/bydate/2011-01-01/2017-01-02', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 1)
        announcement_id = response.data[0]['id']
            
        # Approve Submitted Announcement
        response = self.client.put('/announcement/manage/approve/' + str(announcement_id), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)

        # Get announcements by date
        response = self.client.get('/announcement/bydate/2011-01-01/2017-01-02')
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['title'], 'Test Announcement')
        self.assertEqual(response.data[0]['summary'], 'Test Summary 1')
        self.assertEqual(response.data[0]['pushDate'], '2017-01-01')
        self.assertEqual(response.data[0]['isEvent'], False)
        self.assertEqual(response.data[0]['hostFirstName'], 'Host')
        self.assertEqual(response.data[0]['hostLastName'], 'One')
        self.assertEqual(response.data[0]['hostOrganization'], 'Organization 1')
        self.assertEqual(response.data[0]['hostEmail'], 'hostone@org1.com')
        self.assertEqual(response.data[0]['hostPhone'], '1112223333')
        announcement_id = response.data[0]['id']
        
         # Get announcement detail
        response = self.client.get('/announcement/' + str(announcement_id))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data['detail'], 'Test Detail 1')
        
        # Submit announcement (same applicant email)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)

        # Get announcements for management
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 2)


    def test_submitGetApproveDeclineDeleteAccess(self):
        # Admin register
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # Admin login
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)
        token = response.data['token']
        
        # Get current time
        now_time = timezone.now()
        now_date = now_time.strftime('%Y-%m-%d')

        # Submit announcement (no title)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit announcement (no summary)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit announcement (no detail)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "summary": "Test Submit Summary", "pushDate": now_date,
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit announcement (no push date)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "summary": "Test Submit Summary", "detail": "Test Submit Detail", 
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit announcement (wrong push date)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": "NOTADATE",
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit announcement (no host first name)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "false",
                "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit announcement (no host email)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit announcement
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Announcement", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "false",
                "hostFirstName": "Host", "hostLastName": "One", "hostOrganization": "Organization 1", "hostEmail": "hostone@org1.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)

        # Submit event (wrong non-json request)
        response = self.client.post('/announcement/submit', {
            "title": "Test Submit Event", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
            "isEvent": "true", "eventTime": now_date + " 12:00:00", "locationName": "Some Location", "locationDesc": "Room 101", "locationLat": "16.384", "locationLng": "32.768", "foodProvided": "false",
            "hostFirstName": "Host", "hostLastName": "Two", "hostOrganization": "Organization 2", "hostEmail": "hosttwo@org2.com", "hostPhone": "1112223333"
            })
        self.assertEqual(response.status_code, 500)
        
        # Submit event (event time not in push date)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Event", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "true", "eventTime": "2016-01-01 12:00:00", "locationName": "Some Location", "locationDesc": "Room 101", "locationLat": "16.384", "locationLng": "32.768", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Two", "hostOrganization": "Organization 2", "hostEmail": "hosttwo@org2.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit event (wrong event time)
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Event", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "true", "eventTime": "12:00:00", "locationName": "Some Location", "locationDesc": "Room 101", "locationLat": "16.384", "locationLng": "32.768", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Two", "hostOrganization": "Organization 2", "hostEmail": "hosttwo@org2.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Submit event
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Event", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": now_date,
                "isEvent": "true", "eventTime": now_date + " 12:00:00", "locationName": "Some Location", "locationDesc": "Room 101", "locationLat": "16.384", "locationLng": "32.768", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Two", "hostOrganization": "Organization 2", "hostEmail": "hosttwo@org2.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)

        # Get submitted announcement for management (wrong token)
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": "233"})
        self.assertEqual(response.status_code, 401)
        
        # Get submitted announcement for management
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 2)
        for index, item in enumerate(response.data):
            if item['title'] == 'Test Submit Announcement':       
                self.assertEqual(item['summary'], 'Test Submit Summary')
                self.assertEqual(item['pushDate'], now_date)
                self.assertEqual(item['isEvent'], False)
                self.assertEqual(item['hostFirstName'], 'Host')
                self.assertEqual(item['hostLastName'], 'One')
                self.assertEqual(item['hostOrganization'], 'Organization 1')
                self.assertEqual(item['hostEmail'], 'hostone@org1.com')
                self.assertEqual(item['hostPhone'], '1112223333')
                self.assertEqual(item['status'], 'Unapproved')
            elif item['title'] == 'Test Submit Event':
                self.assertEqual(item['isEvent'], True)
                self.assertEqual(item['eventTime'], now_date + ' 12:00:00')
                self.assertEqual(item['foodProvided'], False)
                self.assertEqual(item['locationName'], 'Some Location')
                self.assertEqual(item['locationDesc'], 'Room 101')
                self.assertEqual(item['locationLat'], 16.384)
                self.assertEqual(item['locationLng'], 32.768)
                self.assertEqual(item['hostLastName'], 'Two')
                self.assertEqual(item['status'], 'Unapproved')
                event_index = index
                event_id = item['id']
        
        # Approve submitted event (wrong id)
        response = self.client.put('/announcement/manage/approve/' + str(233), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 404)
        
        # Approve submitted event
        response = self.client.put('/announcement/manage/approve/' + str(event_id), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        for item in response.data:
            if item['title'] == 'Test Submit Event':
                self.assertEqual(item['status'], 'Approved')
        
        # Get today's announcement
        response = self.client.get('/announcement')
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['title'], 'Test Submit Event')
        
        # Decline submitted event (wrong id)
        response = self.client.put('/announcement/manage/decline/' + str(233), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 404)
        
        # Decline submitted event
        response = self.client.put('/announcement/manage/decline/' + str(event_id), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        # Decline submitted event (with reason)
        response = self.client.put('/announcement/manage/decline/' + str(event_id), JSONRenderer().render(
            {"reason": "test reason"}), content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        for item in response.data:
            if item['title'] == 'Test Submit Event':
                self.assertEqual(item['status'], 'Declined')
                
        # Get today's announcement (empty)
        response = self.client.get('/announcement')
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data, [])
        
        # Delete submitted event (wrong id)
        response = self.client.delete('/announcement/manage/delete/' + str(233), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 404)
        
        # Delete submitted event (unauthorized)
        response = self.client.delete('/announcement/manage/delete/' + str(event_id))
        self.assertEqual(response.status_code, 401)
        
        # Delete submitted event
        response = self.client.delete('/announcement/manage/delete/' + str(event_id), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        # Get announcement
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['title'], 'Test Submit Announcement')
        announcement_id = response.data[0]['id']
        
        # Modify submitted announcement to an event (wrong id)
        response = self.client.put('/announcement/manage/modify/' + str(233), JSONRenderer().render(
            {
                "title": "Modified Test Submit Event", "summary": "Modified Test Submit Summary", "detail": "Modified Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "true", "eventTime": "2017-01-01 18:00:00", "locationName": "Some Other Location", "locationDesc": "Room 202", "locationLat": "65.536", "locationLng": "131.072", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostPhone": "4445556666"
            }),
            content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 404)
        
        # Modify submitted announcement to an event (wrong event time)
        response = self.client.put('/announcement/manage/modify/' + str(announcement_id), JSONRenderer().render(
            {
                "title": "Modified Test Submit Event", "summary": "Modified Test Submit Summary", "detail": "Modified Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "true", "eventTime": "NOT A TIME", "locationName": "Some Other Location", "locationDesc": "Room 202", "locationLat": "65.536", "locationLng": "131.072", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostPhone": "4445556666"
            }),
            content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 400)
        
        # Modify submitted announcement to an event (event time not in push date)
        response = self.client.put('/announcement/manage/modify/' + str(announcement_id), JSONRenderer().render(
            {
                "title": "Modified Test Submit Event", "summary": "Modified Test Submit Summary", "detail": "Modified Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "true", "eventTime": "2017-02-02 19:00:00", "locationName": "Some Other Location", "locationDesc": "Room 202", "locationLat": "65.536", "locationLng": "131.072", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostPhone": "4445556666"
            }),
            content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 400)
        
        # Modify submitted announcement to an event
        response = self.client.put('/announcement/manage/modify/' + str(announcement_id), JSONRenderer().render(
            {
                "title": "Modified Test Submit Event", "summary": "Modified Test Submit Summary", "detail": "Modified Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "true", "eventTime": "2017-01-01 18:00:00", "locationName": "Some Other Location", "locationDesc": "Room 202", "locationLat": "65.536", "locationLng": "131.072", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Three", "hostOrganization": "Organization 3", "hostPhone": "4445556666"
            }),
            content_type='application/json', **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
    
        # Get modified event
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['title'], 'Modified Test Submit Event')
        self.assertEqual(response.data[0]['locationName'], 'Some Other Location')
        self.assertEqual(response.data[0]['foodProvided'], False)
        self.assertEqual(response.data[0]['hostOrganization'], 'Organization 3')
        self.assertEqual(response.data[0]['status'], 'Unapproved')
        
        # Wrongly attend unapproved announcement
        response = self.client.post('/announcement/' + str(announcement_id) + '/attend', JSONRenderer().render(
            {
                "firstName": "Attendant", "lastName": "One", "organization": "Organization A1", "email": "attendantone@a1.com", "phone": "3332221111"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 400)
        
        # Approve submitted event
        response = self.client.put('/announcement/manage/approve/' + str(announcement_id), **{"HTTP_TOKEN": token})
        self.assertEqual(response.status_code, 200)
        
        response = self.client.get('/announcement/manage', **{"HTTP_TOKEN": token})
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['status'], 'Approved')
        
        # Attend submitted event
        response = self.client.post('/announcement/' + str(announcement_id) + '/attend', JSONRenderer().render(
            {
                "firstName": "Attendant", "lastName": "One", "organization": "Organization A1", "email": "attendantone@a1.com", "phone": "3332221111"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # Get attendants of event
        response = self.client.get('/announcement/manage/attendants/' + str(announcement_id), **{"HTTP_TOKEN": token})
        self.assertEqual(len(response.data['attendants']), 1)
        self.assertEqual(response.data['attendants'][0]['firstName'], 'Attendant')
        self.assertEqual(response.data['attendants'][0]['lastName'], 'One')
        self.assertEqual(response.data['attendants'][0]['organization'], 'Organization A1')
        self.assertEqual(response.data['attendants'][0]['email'], 'attendantone@a1.com')
        self.assertEqual(response.data['attendants'][0]['phone'], '3332221111')

    def test_accessRetrieve(self):
        # User register
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # User login
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)
        token = response.data['token']
        
        # Submit event
        response = self.client.post('/announcement/submit', JSONRenderer().render(
            {
                "title": "Test Submit Event", "summary": "Test Submit Summary", "detail": "Test Submit Detail", "pushDate": "2017-01-01",
                "isEvent": "true", "eventTime": "2017-01-01 12:00:00", "locationName": "Some Location", "locationDesc": "Room 101", "locationLat": "16.384", "locationLng": "32.768", "foodProvided": "false",
                "hostFirstName": "Host", "hostLastName": "Two", "hostOrganization": "Organization 2", "hostEmail": "hosttwo@org2.com", "hostPhone": "1112223333"
            }),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        email = "hosttwo@org2.com"
        
        # Access by code (no access code)
        response = self.client.post('/user/access',
            JSONRenderer().render({"email": email}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)
        
        # Access by code (no email)
        response = self.client.post('/user/access',
            JSONRenderer().render({"accessCode": "12345678"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)
        
        # Access by code (wrong email)
        response = self.client.post('/user/access',
            JSONRenderer().render({"email": "notanemail", "accessCode": "12345678"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 401)
        
        # Access by code (wrong code; correct code is sent by email only and not testable)
        response = self.client.post('/user/access',
            JSONRenderer().render({"email": email, "accessCode": "12345678"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 401)
        
        # Retrieve access code
        response = self.client.post('/user/retrievecode',
            JSONRenderer().render({"email": email}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)
        
        # Retrieve access code (no email)
        response = self.client.post('/user/retrievecode',
            JSONRenderer().render({}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)
        
        # Retrieve access code (wrong email format)
        response = self.client.post('/user/retrievecode',
            JSONRenderer().render({"email": "notanemail"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)
        
        # Retrieve access code (wrong email)
        response = self.client.post('/user/retrievecode',
            JSONRenderer().render({"email": "not@exist.com"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 404)

        
class UserTestCases(APITestCase):
    def test_registerLoginChangepwd(self):

        # User register (bad pwd)
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "testadmin", "password": "ha"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)
    
        # User register
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # Wrong User register (Username Too Short)
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "test", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)

        # Wrong user login (No username)
        response = self.client.post('/user/login',
            JSONRenderer().render({"password": "notthispwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)

        # Wrong user login (No pwd)
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "rando"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)
        
        # User login
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)

        # Wrong user login (No user)
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "rando", "password": "notthispwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 401)
        
        # Wrong user login (Wrong Password)
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "notthispwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 401)


        # User change password (bad object)
        response = self.client.post('/user/changepwd',
            JSONRenderer().render({"username": "rando", "new_pwd": "newtestpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 401)

        # User change password (password too short)
        response = self.client.post('/user/changepwd',
            JSONRenderer().render({"username": "rando", "new_pwd": "ha"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)

        # User change password (No user name)
        response = self.client.post('/user/changepwd',
            JSONRenderer().render({}),
            content_type='application/json')
        self.assertEqual(response.status_code, 422)
        
        
        # User change password
        response = self.client.post('/user/changepwd',
            JSONRenderer().render({"username": "testadmin", "new_pwd": "newtestpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)


        # Wrong user login (Old Password)
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 401)
        
        # User login (New Password)
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "newtestpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)

    
class DebugTestCases(APITestCase):
    def test_debug(self):
        # User register
        response = self.client.post('/user/register',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 201)
        
        # User login
        response = self.client.post('/user/login',
            JSONRenderer().render({"username": "testadmin", "password": "testpwd"}),
            content_type='application/json')
        self.assertEqual(response.status_code, 200)
        token = response.data['token']
    
        # Get upload info
        response = self.client.get('/upload/info')
        self.assertEqual(response.status_code, 200)
        
        # Get all announcements
        response = self.client.get('/announcement/all')
        self.assertEqual(response.status_code, 200)
        
        # Get all announcements with management info
        response = self.client.get('/announcement/manage/all')
        self.assertEqual(response.status_code, 200)