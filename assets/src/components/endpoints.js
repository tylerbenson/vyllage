// This module is to reuse url patterns instead of typing again and again.

var endpoints = {
  resume: '/resume/{documentId}',
  resumeHeader: '/resume/{documentId}/header',
  resumeSections: '/resume/{documentId}/section',
  resumeSection: '/resume/{documentId}/section/{sectionId}',
  resumeComments: '/resume/{documentId}/section/{sectionId}/comment',
  resumeComment: '/resume/{documentId}/section/{sectionId}/comment/{commentId}'
};

module.exports = endpoints;