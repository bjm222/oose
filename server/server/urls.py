"""server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.10/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf.urls import url
from django.conf.urls import include
from django.contrib import admin
from django.conf import settings
from django.contrib.staticfiles import views
from AnnouncementPlus.views import *

urlpatterns = [
    # Commented views will be implemented in the future

    url(r'^admin/doc/',include('django.contrib.admindocs.urls')),
    url(r'^admin', admin.site.urls),
    
    url(r'^announcement$', AnnouncementTodayView.as_view()),
    url(r'^announcement/(?P<id>[0-9]+)$', AnnouncementDetailView.as_view()),
    url(r'^announcement/(?P<id>[0-9]+)/attend$', AnnouncementAttendView.as_view()),
    url(r'^announcement/bydate/(?P<datefrom>(\d{4}-\d{2}-\d{2})+)/(?P<dateto>(\d{4}-\d{2}-\d{2})+)$', AnnouncementBydateView.as_view()),
    
    url(r'^announcement/manage$', AnnouncementManageView.as_view()),
    url(r'^announcement/manage/bydate/(?P<datefrom>(\d{4}-\d{2}-\d{2})+)/(?P<dateto>(\d{4}-\d{2}-\d{2})+)$', AnnouncementManageBydateView.as_view()),
    url(r'^announcement/manage/approve/(?P<id>[0-9]+)$', AnnouncementManageApproveView.as_view()),
    url(r'^announcement/manage/decline/(?P<id>[0-9]+)$', AnnouncementManageDeclineView.as_view()),
    url(r'^announcement/manage/modify/(?P<id>[0-9]+)$', AnnouncementManageModifyView.as_view()),
    url(r'^announcement/manage/delete/(?P<id>[0-9]+)$', AnnouncementManageDeleteView.as_view()),
    url(r'^announcement/manage/attendants/(?P<id>[0-9]+)$', AnnouncementGetAttendantsView.as_view()),
    
    url(r'^announcement/submit$', AnnouncementSubmitView.as_view()),
    
    url(r'^user/login$', AdminLoginView.as_view()),
    url(r'^user/register$', AdminRegisterView.as_view()),
    url(r'^user/changepwd$', AdminChangepwdView.as_view()),
    # url(r'^user/(?P<id>[0-9]+)$', UserDetailView.as_view()),
    url(r'^user/access', UserAccessView.as_view()),
    url(r'^user/retrievecode', UserRetrieveCodeView.as_view()),
    
    url(r'^location/list$', LocationListView.as_view()),
    url(r'^location/add$', LocationAddView.as_view()),
    url(r'^location/delete/(?P<id>[0-9]+)$', LocationDeleteView.as_view()),
    
    url(r'^upload/info$', GetUploadInfoView.as_view()),
    
    url(r'^count$', GetAttendantCountView.as_view()),
    
    url(r'^static/(?P<path>.*)$', views.serve, kwargs = {'show_indexes' : True}),

    # DEBUG
    url(r'^announcement/all$', AnnouncementAllView.as_view()),
    url(r'^announcement/manage/all$', AnnouncementManageAllView.as_view()),
    # url(r'^announcement/manage/unapprove/(?P<id>[0-9]+)$', AnnouncementManageApproveView.as_view()),
    # url(r'^tokentest$', TokenTestView.as_view()),
]
