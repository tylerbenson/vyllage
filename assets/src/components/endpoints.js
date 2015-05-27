// This module is to reuse url patterns instead of typing again and again.

var endpoints = {
  resume: '/resume/{documentId}',
  resumeHeader: '/resume/{documentId}/header',
  resumeSections: '/resume/{documentId}/section',
  resumeSectionOrder: '/resume/{documentId}/section-order',
  resumeSection: '/resume/{documentId}/section/{sectionId}',
  resumeComments: '/resume/{documentId}/section/{sectionId}/comment',
  resumeComment: '/resume/{documentId}/section/{sectionId}/comment/{commentId}',
  askAdvice: '/resume/ask-advice',
  askAdviceSuggestions: '/resume/users',
  askAdviceGenerateLink: '/link/share-document'
};

module.exports = endpoints;