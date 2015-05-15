module.exports = {
  dragSource:  {
    beginDrag: function(component) {
      return {
        item: {
          sectionId: component.props.section.sectionId
        }
      };
    }
  },
  dropTarget: {
    acceptDrop: function (component, item) {
      if (item.sectionId !== component.props.section.sectionId) {
        component.props.moveSection(item.sectionId, component.props.section.sectionId);
      }
    }
  }
}