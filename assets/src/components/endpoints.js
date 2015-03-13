// This module is to reuse url patterns instead of typing again and again.

var endpoints = {
  resume: '/resume/{documentId}',
  resumeSection: '/resume/{documentId}/section/{sectionId}',
  resumeComments: '/resume/{documentId}/section/{sectionId}/comment',
  resumeComment: '/resume/{documentId}/section/{sectionId}/comment/{commentId}'
};

module.exports = endpoints;