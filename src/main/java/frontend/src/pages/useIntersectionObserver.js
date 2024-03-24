import { useEffect, useState, useRef } from 'react';

const useIntersectionObserver = (options) => {
    const [isVisible, setIsVisible] = useState(false);
    const elementRef = useRef(null);

    useEffect(() => {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                setIsVisible(entry.isIntersecting);
            });
        }, options);

        const currentElement = elementRef.current;
        if (currentElement) {
            observer.observe(currentElement);
        }

        console.log(observer + ' observer ')
        console.log(currentElement + ' currentElement ')

        return () => {
            if (currentElement) {
                observer.unobserve(currentElement);
            }
        };


    }, [options]); // 옵션 변경 시 재구성

    return [isVisible, elementRef];
};
export default useIntersectionObserver;
