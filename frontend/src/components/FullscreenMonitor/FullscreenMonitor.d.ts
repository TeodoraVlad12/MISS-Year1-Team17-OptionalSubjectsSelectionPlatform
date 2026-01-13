import React from 'react';

export interface FullscreenMonitorProps {
  open: boolean;
  messages?: string[];
  severity?: 'success' | 'info' | 'warning' | 'error';
  title?: string;
  onClose: () => void;
  onContinue?: () => void;
}

declare const FullscreenMonitor: React.FC<FullscreenMonitorProps>;

export default FullscreenMonitor;