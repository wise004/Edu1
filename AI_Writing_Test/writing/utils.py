import json
from openai import OpenAI
from django.conf import settings

# OpenAI client’ini global tarzda yaratamiz
client = OpenAI(api_key=settings.OPENAI_API_KEY)

def evaluate_writing_with_openai(question, answer):
  """
  This function sends 'question' and 'answer' to the GPT-3.5-turbo model
  and expects a JSON response like:
      {
        "band_score": 6.5,
        "feedback": "Some feedback here"
      }
  Note: The maximum IELTS band score is 9, and the minimum is 0.5.
  """
  # 1. Construct the prompt with explicit instructions regarding band score range (0.5 to 9)
  prompt = f"""
You are an IELTS writing examiner. The maximum IELTS band score is 9 and the minimum is 0.5.
The task question is: {question}
The student's answer is: {answer}

Please evaluate the essay based on IELTS Writing Task 2 criteria and respond with JSON exactly in the following format:
{{
  "band_score": 6.5,
  "feedback": "Some feedback here"
}}

Ensure that the "band_score" value is a decimal number between 0.5 and 9.
"""

  try:
    # 2. Call GPT-3.5-turbo using the new API
    response = client.chat.completions.create(
      model="gpt-3.5-turbo",
      messages=[
        {"role": "system", "content": "You are an IELTS writing evaluator. Return only JSON, with no extra text."},
        {"role": "user", "content": prompt}
      ],
      temperature=0.7
    )

    # 3. Get the response content
    content = response.choices[0].message.content

    # 4. Print token usage for debugging
    usage = response.usage
    print(f"Tokens used - Prompt: {usage.prompt_tokens}, Completion: {usage.completion_tokens}, Total: {usage.total_tokens}")

    # 5. Parse the JSON response
    result = json.loads(content)

    # 6. Ensure required keys are present
    required_keys = ["band_score", "feedback"]
    for key in required_keys:
      if key not in result:
        result[key] = None

    # 7. Convert band_score to float and enforce range [0.5, 9]
    try:
      band = float(result["band_score"])
      if band > 9:
        band = 9.0
      if band < 0.5:
        band = 0.5
      result["band_score"] = band
    except (TypeError, ValueError):
      result["band_score"] = 5.0  # fallback value if conversion fails

    return result

  except Exception as e:
    print(f"OpenAI error: {str(e)}")
    return {
      "band_score": None,
      "feedback": f"Error: {str(e)}"
    }
