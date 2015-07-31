// This module is to reuse url patterns instead of typing again and again.

var endpoints = {
  documentId: '/document/user/document-type/{documentType}',
  resume: '/resume/{documentId}',
  resumeHeader: '/resume/{documentId}/header',
  resumeSections: '/resume/{documentId}/section',
  resumeSectionOrder: '/resume/{documentId}/section-order',
  resumeSection: '/resume/{documentId}/section/{sectionId}',
  resumeComments: '/resume/{documentId}/section/{sectionId}/comment',
  resumeComment: '/resume/{documentId}/section/{sectionId}/comment/{commentId}',
  getFeedback: '/resume/get-feedback',
  getFeedbackSuggestions: '/resume/users',
  getFeedbackGenerateLink: '/link/share-document',
  togglz: '/togglz-feature/{feature}/is-active',
  getRoles: '/account/roles'
};

module.exports = endpoints;