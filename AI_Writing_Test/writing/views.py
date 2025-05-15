from rest_framework import generics
from .models import WritingSubmission
from .serializers import WritingSubmissionSerializer
from .utils import evaluate_writing_with_openai
from rest_framework.response import Response
from rest_framework import status

class SubmitWritingView(generics.CreateAPIView):
    queryset = WritingSubmission.objects.all()
    serializer_class = WritingSubmissionSerializer
    # permission_classes = []  # Hamma uchun ochiq

    def perform_create(self, serializer):
        # Foydalanuvchi writingni yubordi:
        question = self.request.data.get('task_question')
        answer = self.request.data.get('user_answer')

        # AI bilan baholaymiz
        ai_result = evaluate_writing_with_openai(question, answer)
        band = ai_result.get('band_score')
        feedback = ai_result.get('feedback')

        # Saqlaymiz
        serializer.save(
            ai_feedback=feedback,
            band_score=band
        )

class ListSubmissionsView(generics.ListAPIView):
    queryset = WritingSubmission.objects.order_by('-created_at')
    serializer_class = WritingSubmissionSerializer
    # permission_classes = []  # Hamma uchun ochiq
