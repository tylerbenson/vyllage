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
      component.props.moveSubSection(item.sectionPosition, component.props.sectionPosition);
    }
  }
}