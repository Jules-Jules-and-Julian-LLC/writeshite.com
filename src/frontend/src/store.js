import React, {createContext, useReducer} from 'react';

const initialState = {};
const store = createContext(initialState);
const { Provider } = store;

const StateProvider = ({children}) => {
  const [state, dispatch] = useReducer((state, action) => {
    switch(action.type) {
      case 'SET':
        return newState;
      default:
        throw new Error();
    };
  }, initialState);