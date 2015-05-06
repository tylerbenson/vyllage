module.exports = {
  dragSource:  {
    beginDrag: function(component) {
      return {
        item: {
          sectionPosition : component.props.section.sectionPosition
        }
      };
    }
  },
  dropTarget: {
    over: function (component, item) {
      component.props.moveSection(item.sectionPosition, component.props.sectionPosition);
    }
  }
}