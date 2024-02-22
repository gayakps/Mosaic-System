import logo from './logo.svg';
import './App.css';
import FileUpload from "./pages/FileUploader";
import RawVideoFileUpload from "./pages/RawVideoFileUpload";

function App() {
  return (
    <div className="App">
      <header className="App-header">
          <RawVideoFileUpload></RawVideoFileUpload>
      </header>
    </div>
  );
}

export default App;
