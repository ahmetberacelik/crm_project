import React from 'react';
import {
  Chip,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import type { TicketResponse } from '../api/tickets.api';
import { useState } from 'react';
import { TicketDetailDialog } from './TicketDetailDialog';

function priorityColor(priority?: string) {
  switch ((priority || '').toUpperCase()) {
    case 'HIGH':
      return 'error';
    case 'MEDIUM':
      return 'warning';
    case 'LOW':
      return 'success';
    default:
      return 'default';
  }
}

export const TicketsTable: React.FC<{ items: TicketResponse[] }> = ({ items }) => {
  const [openId, setOpenId] = useState<number | null>(null);

  return (
    <TableContainer component={Paper} elevation={2}>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell width={90}>ID</TableCell>
            <TableCell>Konu</TableCell>
            <TableCell width={180}>Kategori</TableCell>
            <TableCell width={110}>Öncelik</TableCell>
            <TableCell width={140}>Durum</TableCell>
            <TableCell width={220}>Atama</TableCell>
            <TableCell align="right" width={120}>İşlemler</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {items.map((t) => {
            const firstAssign = t.assignments && t.assignments[0];
            return (
              <TableRow key={t.id} hover>
                <TableCell>#{t.id}</TableCell>
                <TableCell>{t.issue}</TableCell>
                <TableCell>
                  <Chip label={"—"} size="small" />
                </TableCell>
                <TableCell>
                  <Chip label={t.priority} color={priorityColor(t.priority) as any} size="small" />
                </TableCell>
                <TableCell>
                  <Chip label={t.isActive ? 'OPEN' : 'CLOSED'} size="small" />
                </TableCell>
                <TableCell>
                  {firstAssign?.assignee?.name || '—'}
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Detay">
                    <IconButton size="small" onClick={() => setOpenId(t.id)}>
                      <VisibilityIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
      <TicketDetailDialog open={openId !== null} onClose={() => setOpenId(null)} ticketId={openId} />
    </TableContainer>
  );
};


