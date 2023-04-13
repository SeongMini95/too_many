const POSITION = 'position';

export const positionProvider = {
    getPosition: () => {
        return localStorage.getItem(POSITION) ?? '';
    },
    setPosition: (position) => {
        localStorage.setItem(POSITION, position);
    },
    removePosition: () => {
        localStorage.removeItem(POSITION);
    }
}