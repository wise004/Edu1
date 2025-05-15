from django.db import models

class WritingSubmission(models.Model):
    task_question = models.TextField()
    user_answer = models.TextField()
    ai_feedback = models.TextField(blank=True, null=True)
    band_score = models.DecimalField(max_digits=3, decimal_places=1, blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Submission #{self.id} - {self.created_at.date()}"
