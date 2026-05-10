# Requirements Document

## Introduction

The EduNest Chatbot ("Nesty") is an in-portal conversational assistant that helps students, instructors, and admins navigate the course portal. The current implementation is a fully client-side, rule-based widget with a fixed regex knowledge base. This feature evolves Nesty into a robust, context-aware assistant that can answer questions about the platform, surface personalised information from the user's own account (enrolled courses, progress, quiz scores, streaks, bookmarks, notes), and gracefully handle topics it cannot answer. The chatbot remains embedded in the frontend as a floating widget and communicates with a new Spring Boot backend endpoint for AI-assisted responses while retaining a fast rule-based fallback for common FAQ queries.

## Glossary

- **Chatbot**: The "Nesty" conversational assistant widget embedded in the EduNest portal.
- **Chatbot_Widget**: The client-side floating UI component (button + chat window) rendered on every portal page.
- **Chatbot_Backend**: The Spring Boot REST controller and service layer that processes chat messages and returns responses.
- **Knowledge_Base**: The structured set of FAQ rules and platform-specific information used to generate rule-based responses.
- **AI_Provider**: An external large-language-model API (e.g., OpenAI, Google Gemini) used for open-ended questions beyond the Knowledge_Base.
- **Conversation_Session**: A single continuous chat interaction between a user and the Chatbot, identified by a session token, lasting until the user closes the widget or the session expires.
- **Message**: A single text input submitted by the user or a text response returned by the Chatbot.
- **User_Context**: Personalised data about the authenticated user (enrolled courses, progress percentages, quiz scores, streak count, bookmarks, notes) fetched from the portal's existing APIs.
- **Student**: A portal user with role STUDENT.
- **Instructor**: A portal user with role INSTRUCTOR.
- **Admin**: A portal user with role ADMIN.
- **Unauthenticated_User**: A visitor who has not logged in.
- **Fallback_Response**: A response returned when neither the Knowledge_Base nor the AI_Provider can produce a confident answer.
- **Rate_Limit**: The maximum number of AI-assisted messages a user may send within a rolling 60-second window.

---

## Requirements

### Requirement 1: Chatbot Widget Availability

**User Story:** As a portal visitor, I want the chatbot widget to be accessible on every page, so that I can get help without navigating away from my current task.

#### Acceptance Criteria

1. THE Chatbot_Widget SHALL render a floating action button on every HTML page that includes `chatbot.js`.
2. WHEN the floating action button is clicked, THE Chatbot_Widget SHALL open the chat window within 200 ms.
3. WHEN the chat window is open and the close button is clicked, THE Chatbot_Widget SHALL close the chat window within 200 ms.
4. WHILE the chat window is open, THE Chatbot_Widget SHALL be positioned above all other page content (z-index precedence) and SHALL accept user input regardless of the current page scroll position.
5. THE Chatbot_Widget SHALL display an opening greeting message exactly once per Conversation_Session, on the first time the chat window is opened after page load, and SHALL NOT repeat the greeting if the window is closed and reopened within the same session.
6. IF the user submits an empty or whitespace-only Message, THEN THE Chatbot_Widget SHALL NOT send the Message and SHALL NOT display it in the message list.

---

### Requirement 2: Rule-Based FAQ Responses

**User Story:** As a student, I want the chatbot to instantly answer common questions about the platform, so that I do not have to search through documentation.

#### Acceptance Criteria

1. WHEN a Message matches a pattern in the Knowledge_Base, THE Chatbot_Backend SHALL return a response within 500 ms without calling the AI_Provider.
2. THE Knowledge_Base SHALL contain at least one pattern-response entry for each of the following topics: registration, login, password reset, course browsing, enrollment, progress tracking, quizzes, certificates, leaderboard, streaks, bookmarks, notes, discussions, profile management, theme switching, admin functions, instructor functions, free courses, paid courses, and Google Sign-In.
3. WHEN a Message matches multiple Knowledge_Base patterns, THE Chatbot_Backend SHALL return the response for the pattern whose keyword set is the largest subset of the message's words (most specific match).
4. THE Knowledge_Base SHALL support case-insensitive pattern matching.
5. WHEN a Message does not match any Knowledge_Base pattern, THE Chatbot_Backend SHALL NOT return a Knowledge_Base response, and SHALL proceed to the AI_Provider or Fallback_Response path.

---

### Requirement 3: Personalised User Context Responses

**User Story:** As a student, I want the chatbot to answer questions about my own account data (my courses, my progress, my scores), so that I get relevant personalised help without leaving the chat.

#### Acceptance Criteria

1. WHEN an authenticated user asks about their enrolled courses, THE Chatbot_Backend SHALL include the user's current enrollment list and progress percentages in the response.
2. WHEN an authenticated user asks about their quiz scores, THE Chatbot_Backend SHALL include the user's most recent quiz attempt score and total possible score per lesson in the response.
3. WHEN an authenticated user asks about their streak, THE Chatbot_Backend SHALL include the user's current streak count and last activity date in the response.
4. WHEN an authenticated user asks about their bookmarks, THE Chatbot_Backend SHALL include the count and titles of the user's bookmarked lessons in the response.
5. IF an unauthenticated user asks a question that requires User_Context, THEN THE Chatbot_Backend SHALL return a response containing only a login-required message, with no user data fields present in the response body.
6. WHILE fetching User_Context, THE Chatbot_Backend SHALL complete each context fetch operation within 1000 ms before composing the response.
7. IF a User_Context fetch operation exceeds 1000 ms, THEN THE Chatbot_Backend SHALL return a Fallback_Response indicating that personalised data is temporarily unavailable.
8. WHEN an authenticated user asks about their enrolled courses, quiz scores, streak, or bookmarks and the user has no records for that data type, THE Chatbot_Backend SHALL return a response stating that no data is available for that category rather than an empty or null value.

---

### Requirement 4: AI-Assisted Open-Ended Responses

**User Story:** As a student, I want the chatbot to answer open-ended learning questions that go beyond the FAQ, so that I can get study help without leaving the portal.

#### Acceptance Criteria

1. WHEN a Message does not match any Knowledge_Base pattern, THE Chatbot_Backend SHALL forward the Message to the AI_Provider and return the AI_Provider's response.
2. WHEN the AI_Provider returns a response, THE Chatbot_Backend SHALL relay the response to the Chatbot_Widget within 10 seconds measured from the time the Message is received by the Chatbot_Backend.
3. IF the AI_Provider is unavailable or returns an error, THEN THE Chatbot_Backend SHALL return a Fallback_Response containing a user-facing message indicating temporary unavailability, with no internal error identifiers, provider names, or stack traces included.
4. THE Chatbot_Backend SHALL include a system prompt instructing the AI_Provider to answer only questions relevant to online learning and the EduNest platform, and to return a response indicating the question is outside the supported scope for off-topic requests.
5. WHERE the AI_Provider integration is disabled via configuration, THE Chatbot_Backend SHALL return a Fallback_Response for all Messages that do not match the Knowledge_Base.
6. IF the AI_Provider returns a malformed, empty, or null response, THEN THE Chatbot_Backend SHALL treat it as an error and return a Fallback_Response.

---

### Requirement 5: Conversation Session Management

**User Story:** As a student, I want the chatbot to remember the context of our conversation during a session, so that I can ask follow-up questions naturally.

#### Acceptance Criteria

1. WHEN the first Message of a new Conversation_Session is received, THE Chatbot_Backend SHALL assign a unique session identifier and return it to the Chatbot_Widget in the response.
2. WHILE a Conversation_Session is active, THE Chatbot_Backend SHALL retain the last 10 Message exchanges as context when calling the AI_Provider.
3. IF a Conversation_Session has received no Messages for 30 minutes, THEN THE Chatbot_Backend SHALL expire the session and discard its stored context.
4. WHEN a Conversation_Session expires, THE Chatbot_Widget SHALL display a notification to the user that the session has expired and context has been reset, and SHALL start a new Conversation_Session on the next Message without requiring a page reload.
5. THE Chatbot_Backend SHALL NOT persist conversation history to the database.
6. IF the Chatbot_Backend receives a Message with an expired or unrecognised session identifier, THEN THE Chatbot_Backend SHALL create a new Conversation_Session, assign a new session identifier, and process the Message normally.

---

### Requirement 6: Rate Limiting

**User Story:** As a system operator, I want to limit the number of AI-assisted messages per user per minute, so that the portal is protected from excessive AI API costs.

#### Acceptance Criteria

1. THE Chatbot_Backend SHALL enforce a Rate_Limit of 20 AI-assisted Messages per user per 60-second rolling window.
2. WHEN a user exceeds the Rate_Limit, THE Chatbot_Backend SHALL return an HTTP 429 response with a JSON body containing a human-readable message and an ISO 8601 timestamp indicating when the user may send the next Message.
3. WHEN a user exceeds the Rate_Limit, THE Chatbot_Widget SHALL display the rate-limit message to the user and disable the send button until the ISO 8601 retry timestamp has passed.
4. THE Rate_Limit SHALL apply per authenticated user ID for authenticated users, and per client IP address for unauthenticated users.
5. IF the rate-limit store is unavailable, THEN THE Chatbot_Backend SHALL allow the Message to proceed rather than blocking the user, and SHALL NOT increment the counter for that request.

---

### Requirement 7: Message Input and Display

**User Story:** As a student, I want a clear and accessible chat interface, so that I can type messages and read responses comfortably.

#### Acceptance Criteria

1. THE Chatbot_Widget SHALL accept text input up to 500 characters per Message.
2. IF a user attempts to submit a Message exceeding 500 characters, THEN THE Chatbot_Widget SHALL display an inline validation error and SHALL NOT submit the Message.
3. WHEN the user presses the Enter key in the input field, THE Chatbot_Widget SHALL submit the Message.
4. WHILE a response is being fetched, THE Chatbot_Widget SHALL display a typing indicator animation.
5. WHILE a response is being fetched, THE Chatbot_Widget SHALL disable the send button.
6. WHEN a response is received, THE Chatbot_Widget SHALL remove the typing indicator.
7. WHEN a response is received, THE Chatbot_Widget SHALL display the response in the message list.
8. THE Chatbot_Widget SHALL render bot responses that contain HTML anchor tags as clickable hyperlinks opening in a new tab.
9. WHEN a new Message or response is added to the message list, THE Chatbot_Widget SHALL scroll the message list to the most recent item.
10. WHEN the chat window is first opened, THE Chatbot_Widget SHALL display between 3 and 6 quick-reply suggestion chips for common queries, and WHEN a chip is clicked, THE Chatbot_Widget SHALL submit its label as a Message.
11. IF a response fetch fails due to a network error or server error, THEN THE Chatbot_Widget SHALL remove the typing indicator, re-enable the send button, and display an error message in the message list.

---

### Requirement 8: Security and Data Privacy

**User Story:** As a system operator, I want the chatbot to handle user data securely, so that no user can access another user's personal information through the chatbot.

#### Acceptance Criteria

1. WHEN the Chatbot_Backend fetches User_Context, THE Chatbot_Backend SHALL use the authenticated user's ID from the request header and SHALL NOT accept a user ID supplied in the Message body.
2. THE Chatbot_Backend SHALL sanitise all Message content before including it in prompts sent to the AI_Provider by stripping all HTML tags and script content, preserving only plain text, and truncating to a maximum of 2000 characters.
3. THE Chatbot_Backend SHALL NOT include sensitive fields (password, passwordResetToken) in any User_Context passed to the AI_Provider.
4. IF a Message contains instructions directing the AI to override, ignore, or bypass its system prompt, THEN THE Chatbot_Backend SHALL log the attempt and return a Fallback_Response without forwarding the Message to the AI_Provider.
5. WHEN the Chatbot_Backend receives a Message, THE Chatbot_Backend SHALL validate that the session identifier in the request belongs to the requesting user before processing the Message.
6. IF the session identifier in the request does not belong to the requesting user, THEN THE Chatbot_Backend SHALL reject the request with an HTTP 403 response and SHALL NOT process the Message.

---

### Requirement 9: Backend REST API

**User Story:** As a developer, I want a well-defined REST API for the chatbot, so that the frontend widget and future integrations can communicate with the backend reliably.

#### Acceptance Criteria

1. THE Chatbot_Backend SHALL expose a `POST /chatbot/message` endpoint that accepts a JSON body containing `message` (string, 1–2000 characters, required), `sessionId` (string, optional), and `userId` (integer, optional for unauthenticated users).
2. WHEN a valid request is received, THE Chatbot_Backend SHALL return a JSON response containing `reply` (string), `sessionId` (string — the provided sessionId if present, or a newly generated one if absent), and `source` (one of: `"knowledge_base"`, `"ai_provider"`, `"fallback"`).
3. IF the request body is missing the `message` field, the `message` field is blank, or the `message` exceeds 2000 characters, THEN THE Chatbot_Backend SHALL return an HTTP 400 response with a JSON body identifying which validation rule was violated.
4. IF the request `Content-Type` header is not `application/json`, THEN THE Chatbot_Backend SHALL return an HTTP 415 response and SHALL NOT process the Message.
5. THE Chatbot_Backend SHALL include a `Access-Control-Allow-Origin` response header consistent with the allowed origins configured in `WebConfig`.

---

### Requirement 10: Accessibility

**User Story:** As a student with accessibility needs, I want the chatbot widget to be usable with a keyboard and screen reader, so that I am not excluded from getting help.

#### Acceptance Criteria

1. THE Chatbot_Widget SHALL assign the following ARIA roles and labels: `role="dialog"` and `aria-label="Nesty chat window"` on the chat window, `role="log"` and `aria-label="Chat messages"` on the message list, `aria-label="Message input"` on the input field, and `aria-label="Send message"` on the send button.
2. WHEN the chat window opens, THE Chatbot_Widget SHALL move keyboard focus to the message input field.
3. WHEN the chat window closes, THE Chatbot_Widget SHALL return keyboard focus to the floating action button.
4. WHEN a new bot response is added to the message list, THE Chatbot_Widget SHALL announce the response text via an ARIA live region with `aria-live="polite"`.
5. IF the user presses the Escape key while the chat window is open, THEN THE Chatbot_Widget SHALL close the chat window.
6. THE Chatbot_Widget SHALL be fully operable using only keyboard navigation (Tab to move between controls, Enter to activate buttons and submit messages).
7. THE Chatbot_Widget SHALL maintain a colour contrast ratio of at least 4.5:1 between text and background colours in both light and dark themes.
