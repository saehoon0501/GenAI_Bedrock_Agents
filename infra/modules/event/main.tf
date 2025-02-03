##################################
# Create an EventBridge Rule (Cron Schedule)
##################################
# Example: cron(0 12 * * ? *) -> triggers at 12:00 UTC every day
# Or rate(5 minutes) -> every 5 minutes
resource "aws_cloudwatch_event_rule" "cron_job" {
  name                = "my-cron-job"
  description         = "Scheduled event for specific task"
  schedule_expression = "cron(0 10 * * ? *)"  # Run at 10:00 AM UTC daily

  tags = var.tags
}

##################################
# Attach the SQS Queue as the Target for the Rule
##################################
resource "aws_cloudwatch_event_target" "my_cron_rule_target" {
  rule = aws_cloudwatch_event_rule.cron_job.name
  arn  = var.event_queue_arn

  # Optionally, include a small JSON message
  input = jsonencode({
    "message" : <<EOT
    "Create a blog post from this article:
    
Structured Outputs
Developer Messages
이러한 기능들이 바로 오늘날 AI를 활용하는 개발자들이 가장 선호하는 요소로 자리매김하고 있습니다.

2. 주요 기능 및 개선 사항
OpenAI o3-mini는 다양한 사용자 요구에 부응할 수 있도록 여러 혁신적인 기능들을 탑재하고 있습니다.

추론 노력 선택 옵션:
Low, Medium, High 세 가지 모드를 제공하여, 사용자는 문제의 복잡성이나 응답 속도에 맞춰 적절한 옵션을 선택할 수 있습니다.
Medium 옵션은 기본값으로 설정되어 있으며, 균형 잡힌 속도와 정확도를 제공합니다.
플랫폼 통합 및 확장성:
ChatGPT, Assistants API, Batch API 등 다양한 플랫폼에서 활용 가능하며, 특히 ChatGPT에서는 사용자가 ‘Reason’ 모드를 선택하거나 응답 재생성을 통해 체험할 수 있습니다.
향상된 응답 속도:
기존 모델 대비 평균 응답 시간이 약 24% 단축되어, 7.7초 내외의 빠른 응답을 제공합니다.
검색 연동 기능:
최신 정보를 반영한 답변과 함께 관련 웹 소스 링크를 제공함으로써, 사용자에게 보다 신뢰성 높은 정보를 전달합니다.
*웹 검색 + 추론이기 때문에 기존 딥시크와도 경쟁이되며, 개인적으로 가장 마음에 드는 부분이였습니다.





3. 성능 평가: STEM, 코딩 및 논리 문제 해결
OpenAI o3-mini는 다양한 분야에서 성능 평가를 통해 그 우수성이 입증되고 있습니다.

경쟁 수학 (AIME 2024):
o3-mini (High) 모드는 83.6%의 정확도를 기록하며, 최신 경쟁 수학 문제에서도 우수한 성능을 보였습니다.
PhD급 과학 문제 (GPQA Diamond):
*o3-mini (High)*는 77.0%의 정확도를 달성, 어려운 과학적 질문에도 탁월한 해답을 제시합니다.
경쟁 코딩 (Codeforces):
Elo 점수 2073을 기록하며, 코딩 대회에서의 실력을 수치로도 증명하였습니다.
소프트웨어 엔지니어링 (SWE-bench Verified):
48.9%의 정확도로, 실무에서 요구되는 소프트웨어 문제 해결 능력 또한 향상되었습니다.


4. 안전성과 신뢰성 강화
높은 추론 능력에 못지않게 OpenAI o3-mini는 안전성과 윤리적 사용에도 큰 비중을 두고 있습니다.

Deliberative Alignment:
AI가 응답하기 전, 인간 작성 안전 지침을 사전에 검토하는 과정을 통해 안전성을 강화하였습니다.
안전 평가 및 검증:
GPT-4o보다 어려운 안전 및 jailbreak 평가에서도 우수한 성능을 보여, 잠재적 위험 요소를 효과적으로 차단합니다.
콘텐츠 필터링 및 사용자 확인:
민감하거나 부적절한 콘텐츠에 대해 자동 차단 기능을 탑재하고 있으며, 돌이킬 수 없는 작업 전 사용자 재확인을 요청하는 등 다층적인 안전 장치를 마련하였습니다.

이번 OpenAI o3-mini의 출시는 비용 효율성과 높은 추론 성능을 동시에 달성한 혁신적인 모델로, AI 기술로서 현재 딥시크 열풍을 어떻게 잠재울 수 있을지 기대됩니다. 오픈소스는 아니기 때문에 딥시크의 열풍은 계속될것인지 성능면에서 o3-mini를 통해 분위기 전환이 될지 계속 지켜보면서 전달해드리도록 하겠습니다.

언제나 글 읽어주셔서 감사합니다.

보표 드림

OpenAI o3-mini FAQ (자주 묻는 질문)
1. 일반적인 질문
Q1. OpenAI o3-mini는 무엇인가요?
A1. OpenAI o3-mini는 OpenAI에서 개발한 최신 모델로, 비용 효율적인 추론 능력을 제공하며 특히 STEM 분야에 강점을 가지고 있습니다.

Q2. o3-mini는 언제부터 사용할 수 있나요?
A2. ChatGPT와 API에서 바로 사용할 수 있습니다.

Q3. o3-mini는 어떤 사용자에게 제공되나요?
A3.
ChatGPT Plus, Team, Pro 사용자: 즉시 이용 가능
Enterprise 사용자: 1주일 후부터 이용 가능
무료 플랜 사용자: 메시지 작성기에서 'Reason' 선택 또는 응답 재생성을 통해 사용 가능

Q4. o3-mini는 이전 모델인 o1-mini와 어떻게 다른가요?
A4. o3-mini는 o1-mini보다 더 빠른 응답 속도와 향상된 추론 능력을 제공하며, 특히 STEM 분야에서 성능이 뛰어납니다.

Q5. o3-mini는 어떤 기능을 지원하나요?
A5. 함수 호출, 구조화된 출력, 개발자 메시지 등의 기능을 지원합니다.

Q6. o3-mini는 시각적 추론 기능을 지원하나요?
A6. 시각적 추론 기능은 지원하지 않으므로, 시각적 추론 작업에는 OpenAI o1을 사용해야 합니다.

Q7. o3-mini는 어떤 API를 통해 사용할 수 있나요?
A7. Chat Completions API, Assistants API, Batch API를 통해 이용할 수 있습니다.

2. 성능 및 기능 질문
Q1. o3-mini는 어떤 분야에서 가장 강력한 성능을 보이나요?
A1. 과학, 수학, 코딩과 같은 STEM 분야에서 뛰어난 성능을 보입니다.

Q2. o3-mini의 추론 노력 수준은 무엇이며, 어떻게 설정하나요?
A2. 낮은, 중간, 높은 세 가지 추론 노력 옵션을 제공하며, 사용 사례에 따라 최적화할 수 있습니다.

Q3. o3-mini의 중간 추론 노력 수준은 이전 모델과 비교했을 때 어떤가요?
A3. 수학, 코딩, 과학 분야에서 o1과 비슷한 성능을 보이며 응답 속도는 더 빠릅니다.

Q4. o3-mini의 높은 추론 노력은 어떤 성능을 보여주나요?
A4. 높은 추론 노력 모드에서는 AIME, GPQA와 같은 어려운 평가에서 더 나은 성능을 보이며, 특히 FrontierMath와 같은 연구 수준의 수학 문제에서 좋은 결과를 나타냅니다.

Q5. o3-mini의 코딩 능력은 어떤가요?
A5. Codeforces와 같은 코딩 대회에서 높은 Elo 점수를 기록하며, SWEbench-verified에서 최고 성능 모델로 평가받고 있습니다.

Q6. o3-mini의 응답 속도는 어느 정도인가요?
A6. o1-mini보다 24% 더 빠른 응답 속도를 보이며, 평균 응답 시간은 7.7초입니다.

Q7. o3-mini는 정확도 측면에서 어떤가요?
A7. 전문가 평가에서 o1-mini보다 더 정확하고 명확한 답변을 제공하며, 오류 발생률도 39% 감소되었습니다.

Q8. o3-mini의 안전성은 어떻게 평가되나요?
A8. 안전 사양에 대한 추론을 하도록 훈련되어 있으며, GPT-4o보다 안전성 및 탈옥 평가에서 더 나은 결과를 보여줍니다.

Q9. o3-mini는 어떤 종류의 콘텐츠를 금지하나요?
A9. 안전을 위해 잠재적으로 유해한 콘텐츠의 생성을 금지하도록 평가되었습니다.

3. 사용 및 업그레이드
Q1. ChatGPT Plus 및 Team 사용자는 o3-mini를 어떻게 사용하나요?
A1. 모델 선택기에서 o3-mini를 선택하여 사용합니다.

Q2. 무료 사용자는 o3-mini를 어떻게 이용할 수 있나요?
A2. 메시지 작성기에서 'Reason'을 선택하거나 응답을 재생성하여 이용할 수 있습니다.

Q3. o3-mini로 업그레이드되면 메시지 제한이 어떻게 되나요?
A3. Plus 및 Team 사용자: 기존 o1-mini의 하루 50개 제한에서 o3-mini 사용 시 150개로 증가

Q4. o3-mini는 검색 기능과 연동되나요?
A4. 최신 정보 검색을 위해 웹 소스 링크와 함께 검색 기능을 지원합니다.

Q5. 유료 사용자는 o3-mini-high를 사용할 수 있나요?
A5. 모든 유료 사용자는 모델 선택기에서 o3-mini-high를 선택할 수 있으며, Pro 사용자는 무제한으로 이용할 수 있습니다.

Q6. o3-mini는 언제부터 o1-mini를 대체하나요?
A6. 모델 선택기에서 o1-mini를 대체하며, 더 높은 속도와 낮은 대기 시간을 제공합니다.

save the result to the S3 bucket."
    EOT
  })    
}
