// GrowWidthOnView.js
import React from 'react';
import useIntersectionObserver from "../pages/useIntersectionObserver";

const GrowWidthOnView = ({ children }) => {
    const [isVisible, elementRef] = useIntersectionObserver({
        threshold: new Array(101).fill(0).map((_, index) => index * 0.01)
    });

    return (
        <div
            ref={elementRef}
            style={{ width: isVisible ? '50%' : '10%' }}
            className="growWidth"
        >
            {children}
        </div>
    );
};

export default GrowWidthOnView;
