from django.urls import path
from .views import SubmitWritingView, ListSubmissionsView

urlpatterns = [
    path('submit-writing/', SubmitWritingView.as_view(), name='submit-writing'),
    path('all-submissions/', ListSubmissionsView.as_view(), name='all-submissions'),
]
