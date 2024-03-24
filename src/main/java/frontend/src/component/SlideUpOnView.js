import React from 'react';
import '../css/SlideUpOnView.css';
import useIntersectionObserver from "../pages/useIntersectionObserver"; // Slide-up 애니메이션을 위한 CSS 파일

const SlideUpOnView = ({ children }) => {
    const [isVisible, elementRef] = useIntersectionObserver({
        threshold: 0.2 // 컴포넌트의 10%가 보이면 트리거
    });

    return (
        <div ref={elementRef} className={`slideUp ${isVisible ? 'visible' : ''}`}>
            {children}
        </div>
    );
};
export default SlideUpOnView;
