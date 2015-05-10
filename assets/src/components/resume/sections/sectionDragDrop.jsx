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
    over: function (component, item) {
      component.props.moveSection(item.sectionId, component.props.section.sectionId);
    }
  }
}