import { createContext, useContext } from 'react';
import type { ReactNode } from 'react';
import { AllocationService } from './AllocationService';
import { BACKEND_BASE_URL } from '../library/constants';

interface Services {
    allocationService: AllocationService;
}

const ServicesContext = createContext<Services | undefined>(undefined);

export const ServicesProvider = ({ children }: { children: ReactNode }) => {
    const services: Services = {
        allocationService: new AllocationService(BACKEND_BASE_URL),
    };

    return (
        <ServicesContext.Provider value={services}>
            {children}
        </ServicesContext.Provider>
    );
};

export const useServices = (): Services => {
    const context = useContext(ServicesContext);
    if (!context) {
        throw new Error('useServices must be used within a ServicesProvider');
    }
    return context;
};
