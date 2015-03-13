var React = require('react');
var request = require('superagent');
var Reflux = require('reflux');

var Header = require('./components/header');
var Profile = require('./components/profile');
var ContactInfo = require('./components/contact/contact');
var ShareInfo = require('./components/contact/share');
var FreeformContainer = require('./components/freeform/container');
var ArticleContent = require('./components/organization/article-content');
var ArticleControlls =require('./components/comments/Controls');
var AddSections = require('./components/addSections/addSections');

var SectionsStore = require('./components/organization/store');
var Actions = require('./components/organization/actions');

var EmptyStateData =[
  {
    "type": "freeform",
    "title": "career goal",
    "sectionPosition": 1,
    "state": "shown",
    "description": ""
  },
  {
    "type": "experience",
    "title": "experience",
    "sectionPosition": 2,
    "state": "shown",
    "organizationName": "",
    "organizationDescription": "",
    "role": "",
    "startDate": "",
    "endDate": "",
    "isCurrent": false,
    "location": "",
    "roleDescription": "",
    "highlights": ""
  }
];

function compare(a,b) {
  if (a.sectionPosition < b.sectionPosition){
    return -1;
  }
  if (a.sectionPosition > b.sectionPosition) {
    return 1;
  }
  return 0;
}

var MainContainer = React.createClass({  
    
    getInitialState: function() {
      return {mainData: SectionsStore.getSections(),
              disableEditMode: SectionsStore.getDisableState()};
    },

    componentDidMount : function() {
      this.unsubscribe = SectionsStore.listen(this.onChange);
    },

    componentWillUnmount: function() {
      this.unsubscribe();
    },

    onChange: function(sections) {
      this.setState({ mainData: sections['sections'],
                      disableEditMode: sections['disableEditMode'] });
    },

    componentDidUpdate: function() {
      var resizingTextareas = [].slice.call(document.querySelectorAll('textarea'));

      resizingTextareas.forEach(function(textarea) {
        textarea.addEventListener('input', autoresize, false);
      });

      function autoresize() {
          this.style.height = 0;
          this.style.height = this.scrollHeight  +'px';
      }
    },

    addSection: function (type, sectionPosition) {
      if(!this.state.disableEditMode) {
        this.state.editModePosition = sectionPosition;
        Actions.addSection(type, sectionPosition);
      }
    },

    // Render freeForm items
    freeFormItems : function(result, addNewSection, firstElement) {

      var AddSectionButtons;

      if(addNewSection || firstElement) {

        AddSectionButtons = ( 
          <div className="u-pull-left full">
            <button className="u-pull-left article-btn"> {result.title} </button>
            <button disabled={this.state.disableEditMode?'disabled':''}  
                    className="u-pull-right article-btn addSection-btn"
                    onClick={this.addSection.bind(null, result.title, result.sectionPosition)}> 
                    {result.title}
            </button>
          </div>
        );
      }

      return (
       <article className = {addNewSection? "career-goal forceEditMode" : firstElement ? "career-goal": "career-goal same-section"}>
        <div className="row" id = {result.sectionId}>
          <div className="twelve columns">

            {AddSectionButtons}

            <FreeformContainer description={result.description} ref="section"/>

            <ArticleControlls/>
          </div>
        </div>
      </article>
      )
    },

    // Render experience items
    experienceItems : function(result, addNewSection, firstElement) {
      var AddSectionButtons;

      if(addNewSection || firstElement) {
        AddSectionButtons = ( 
          <div className="u-pull-left full">
            <button className="u-pull-left article-btn"> {result.title} </button>
            <button className="u-pull-right article-btn addSection-btn"
                    disabled={this.state.disableEditMode?'disabled':''}
                    onClick={this.addSection.bind(null, result.title, result.sectionPosition)}>
                    {result.title}
            </button>
          </div>
        );
      } 
      return (
        <article className = {addNewSection? "experience forceEditMode" : firstElement ? "experience": "experience same-section"}>
          <div className="row">
            <div className="twelve columns">
              {AddSectionButtons}

              <ArticleContent organizationData={result}/>

              <ArticleControlls />
            </div>
          </div>
        </article>
      )
    },

    render: function() {
      var results = this.state.mainData,
          that = this,
          experienceCount = 0,
          educationCount = 0,
          careergoalCount = 0,
          skillsCount = 0,
          addNewItem = 0,
          shouldHideEducation = false, 
          shouldHideSkills = false;

      if(results == undefined || results == '') {
        this.state.mainData = EmptyStateData;
          return (
            <div>
              { 
                EmptyStateData.map(function(result) {
                  if(result.type === "freeform") {
                    return that.freeFormItems(that.state.mainData[0], true,true);
                  } else {
                    return that.experienceItems(that.state.mainData[1], true,true); 
                  }
                })
              }
              <AddSections addSection={that.addSection} title={'education'} position={this.state.mainData.length+1}/>
              <AddSections addSection={that.addSection} title={'skills'} position={this.state.mainData.length+1}/>
            </div>
          );
      } else {
        results.sort(compare);
        return ( 
          <div> 
            {              
              // Create loop for elements.
              results.map(function(result) {
                // Check for item type
                if(result.type === "freeform"){
                  addNewItem = 0;
                    // Check for find edit item position
                  if(that.state.editModePosition == result.sectionPosition) {
                    // Case when have edit mode item.
                    // Check for find first element (career goal), need for add button.
                    if(result.title == "career goal") {
                      addNewItem = 1;
                      careergoalCount += 1;
                    }

                    // Check for find first element (skills), need for add button.
                    if(result.title == "skills") {
                      addNewItem = 1;
                      shouldHideSkills = true;
                      skillsCount += 1;
                    }
                    that.state.editModePosition = undefined;
                    // return element
                    return that.freeFormItems(result, true,true);
                  } else {
                    if(careergoalCount == 0  && result.title == "career goal") {
                      addNewItem = 1;
                      careergoalCount += 1;
                    }

                    if(skillsCount == 0 && result.title == "skills") {
                      addNewItem = 1;
                      skillsCount += 1;
                      shouldHideSkills = true;
                    }

                    if (addNewItem == 1) {
                      addNewItem = 0;
                      return that.freeFormItems(result,false,true);
                    } else {
                      return that.freeFormItems(result,false,false);
                    }
                  }
                } else {
                  addNewItem = 0;
                  if(that.state.editModePosition == result.sectionPosition) {
                    if(result.title == "experience") {
                      addNewItem = 1;
                      experienceCount += 1;
                    }

                    if(result.title == "education"){
                      addNewItem = 1;
                      educationCount += 1;
                      shouldHideEducation = true;
                    }
                     that.state.editModePosition = undefined;
                    return that.experienceItems(result, true,true);

                  } else {
                    if(experienceCount == 0 && result.title == "experience") {
                      addNewItem = 1;
                      experienceCount += 1;
                    }

                    if(educationCount == 0 && result.title == "education") {
                      addNewItem = 1;
                      educationCount += 1;
                      shouldHideEducation = true;
                    }

                    if (addNewItem == 1) {
                      addNewItem = 0;
                      return that.experienceItems(result,false,true);
                    } else {
                      return that.experienceItems(result,false,false);
                    }
                  }
                }
              })
            }

            <AddSections addSection={this.addSection} title={'education'} position={results.length+1} shouldHide = {shouldHideEducation} />
            <AddSections addSection={this.addSection} title={'skills'} position={results.length+1} shouldHide = {shouldHideSkills}/>
          </div>
        );
      }
    }
});

React.render(<MainContainer />, document.getElementById('main-container'));




