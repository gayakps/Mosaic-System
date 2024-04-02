import './App.css';
import SlideUpOnView from "./animation/SlideUpOnView";
import GrowWidthOnView from "./animation/GrowWidthOnView";
import myImage from './css/logo512.png'
import myImage2 from './css/logo192.png'
import RawVideoFileUpload from "./pages/RawVideoFileUpload";


function App() {
    return (
    <div className="App">
      <header className="App-header">

          <div>
              <hr/>
              test-1
              <br/>
              <br/><br/><br/><br/><br/><br/><br/>
              <br/>
              <hr/>
          </div>

          <div>
              test-2
              <br/>
              <br/><br/><br/><br/><br/><br/><br/>
              <br/>
              <hr/>
          </div>


          <GrowWidthOnView>
              <img src={myImage} alt="description" />
          </GrowWidthOnView>

          <div>
              test-3
              <br/>
              <br/><br/><br/><br/><br/><br/><br/>
              <br/>
              <hr/>
          </div>

          <SlideUpOnView>
              <RawVideoFileUpload/>
              <br/>
              <br/>
              <hr/>

          </SlideUpOnView>

          <SlideUpOnView>
              <img src={myImage2} alt="description" /> <br/>

              test
                  <br/>
                  <br/><br/><br/><br/><br/><br/><br/>
                  <br/>
                  <hr/>
          </SlideUpOnView>



      </header>
    </div>
  );
}

export default App;
